package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreDocumentLiveData;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.Target;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusObserver;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType.UserDataUpdateStatusTypeDef;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

import static io.lundie.stockpile.data.repository.UserRepositoryUtils.UserLiveDataStatusType.*;

/**
 * TODO: We need to do null checks in our repo, to re-fetch data if null. Implement this across view models.
 * User Repository is an {@link io.lundie.stockpile.injection.AppScope} class, giving access
 * to user data fetched from FireStore.
 * IMPORTANT: Since user repository has application scope, be very careful when adding
 * {@link MediatorLiveData} sources. Make sure an observer will not be added multiple times.
 */
public class UserRepository extends BaseRepository {

    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;

    private FirestoreDocumentLiveData userDocumentLiveData;
    private MediatorLiveData<UserData> userMediatorData = new MediatorLiveData<>();
    private UserData staticUserData;

    private DocumentSnapshot mostRecentUserDataSnapshot;

    //private MutableLiveData<UserData> userDocumentLiveData = new MutableLiveData<>();
    private MediatorLiveData<ArrayList<Target>> targetsMediatorData = new MediatorLiveData<>();

    private @UserLiveDataStatusTypeDef int userLiveDataStatus;

    @Inject
    UserRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public void initUserDocumentRealTimeUpdates(@NonNull String userID) {
        if(userDocumentLiveData == null || userDocumentLiveData.getValue() == null) {
            Timber.i("UserData --> Getting user live data. UserID : %s", userID );
            beginUserDocumentRealTimeData(userID);
            initUserMediatorData();
        }
    }

    private void initUserMediatorData() {
        if (userMediatorData.getValue() == null) {
            userMediatorData.addSource(userDocumentLiveData, userDocument -> {
                userMediatorData.setValue(userDocument.toObject(UserData.class));
            });
        }
    }

    public LiveData<UserData> getUserMediatorData() {
        return userMediatorData;
    }

//    public ArrayList<Target> getStaticTargetsSnapshot(@NonNull String userID) {
//        if(targetsMediatorData == null || targetsMediatorData.getValue() == null) {
//            initTargetsMediatorData();
//        }
//    }
//
//    public LiveData<ArrayList<Target>> getUpdatingTargetsLiveData() {
//        Timber.e("#UserData --> --> Repo Getter: Target List is setting as: %s", getTargetsMediatorData().getValue());
//        if(targetsMediatorData == null || targetsMediatorData.getValue() == null) {
//            initTargetsMediatorData
//        }
//        return targetsMediatorData;
//    }

    public LiveData<DocumentSnapshot> getUserDocumentRealTimeData() { return userDocumentLiveData;  }

    private void beginUserDocumentRealTimeData(@NonNull String userID) {
        DocumentReference reference = firestore.collection("users").document(userID);
        userDocumentLiveData = new FirestoreDocumentLiveData(reference);
    }

    public UserData fetchMostRecentUserDocumentData(@NonNull String userID) {
        if(userDocumentLiveData != null && userDocumentLiveData.getValue() != null) {
            return userDocumentLiveData.getValue().toObject(UserData.class);
        }

        if(isUserIDEmpty(userID)) return null;

        DocumentReference docRef = firestore.collection("users").document(userID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document!= null && document.exists()) {
                    Timber.d("-->> CatRepo: Firestore: DocumentSnapshot data: %s", document.getData());
                    staticUserData = document.toObject(UserData.class);
                } else {
                    Timber.e("-->> UserRepo:Firestore: No such document");
                }
            } else {
                Timber.e(task.getException(), "Task failed.");
            }
        });
        return staticUserData;
    }

    public void updateTotalCalories(String userID, String categoryName, int calorieChange) {
        if(calorieChange != 0) {
            appExecutors.networkIO().execute(() -> {
                UserData userData = fetchMostRecentUserDocumentData(userID);
                if(userData != null) {
                    ArrayList<ItemCategory> categories = userData.getCategories();
                    for (int i = 0; i < categories.size(); i++) {
                        ItemCategory category = categories.get(i);
                        if(category.getCategoryName().equals(categoryName)) {
                            int totalCalories = category.getTotalCalories() + calorieChange;
                            category.setTotalCalories(totalCalories);
                            categories.set(i, category);
                            userData.setCategories(categories);
                            updateUserData(userID, userData, null);
                            break;
                        }
                    }
                }
            });
        }
    }

    public void addTarget(String userID, Target newTarget, UserDataUpdateStatusObserver observer) {
        Timber.e("Target data is: %s", newTarget.getTargetName());
        setTargetData(userID, newTarget, false, null, observer);
    }

    public void updateTarget(String userID, Target updatedTarget, String originalTitle,
                             UserDataUpdateStatusObserver observer) {
        //TODO: implement update
    }

    private void setTargetData(String userID, Target targetData, Boolean isUpdate,
                              @Nullable String originalTitle, UserDataUpdateStatusObserver observer) {

        updateObserver(observer, UserDataUpdateStatusType.UPDATING);
        appExecutors.networkIO().execute(() -> {
            UserData userData = fetchMostRecentUserDocumentData(userID);
            if(userData != null) {
                ArrayList<Target> targets = userData.getTargets();
                if(isUpdate) {
                    // Remove the original item
                    if(originalTitle != null) {
                        for (int i = 0; i < targets.size(); i++) {
                            Target target = targets.get(i);
                            if (target.getTargetName().equals(originalTitle)) {
                                targets.remove(i);
                                break;
                            }
                        }
                    }
                }
                if(targets == null) {
                    targets = new ArrayList<>();
                }
                Timber.e("Targets data is: %s", targets.size());
                targets.add(targetData);
                userData.setTargets(targets);
                updateUserData(userID, userData, observer);
            } else {
                updateObserver(observer,UserDataUpdateStatusType.FAILED);
            }
        });
    }

    private void updateUserData(String userID, UserData userData, UserDataUpdateStatusObserver observer) {
        DocumentReference documentReference = firestore.collection("users")
                .document(userID);
        documentReference.set(userData)
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                updateObserver(observer, UserDataUpdateStatusType.SUCCESS);
                Timber.i("UserData --> Successfully updated user data.");
            } else {
                updateObserver(observer, UserDataUpdateStatusType.FAILED);
                Timber.e(task.getException(), "Error updating document.");
            }
        });
    }

    //TODO: remove or update
    private void updateObserver(UserDataUpdateStatusObserver observer,
                                @UserDataUpdateStatusTypeDef int status) {
        if(observer != null) {
            observer.update(status);
        }
    }

}