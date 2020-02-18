package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreDocumentLiveData;
import io.lundie.stockpile.data.FirestoreLiveDataListener;
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
public class UserRepository extends Observable {

    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;

    private MediatorLiveData<String> userDisplayName = new MediatorLiveData<>();

    private FirestoreDocumentLiveData userLiveData;

    private DocumentSnapshot mostRecentUserDataSnapshot;

    //private MutableLiveData<UserData> userLiveData = new MutableLiveData<>();
    private MediatorLiveData<ArrayList<Target>> targetsMediatorData = new MediatorLiveData<>();

    private @UserLiveDataStatusTypeDef int userLiveDataStatus;

    @Inject
    UserRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public MutableLiveData<String> getUserDisplayName() {
        return userDisplayName;
    }

    public UserData getUserDataFromLiveData(@NonNull String userID) {
        if(userLiveData == null || userLiveData.getValue() == null) {
            Timber.i("UserData --> Getting user live data. UserID : %s", userID );
            fetchUserLiveData(userID);
            initUserMediatorData();
        } else {
            return userLiveData.getValue().toObject(UserData.class);
        } return null;
    }

    private void initUserMediatorData() {
        if(userDisplayName.getValue() == null) {
            userDisplayName.addSource(userLiveData, snapshot -> {
                UserData data = snapshot.toObject(UserData.class);
                if(data != null) {
                    userDisplayName.setValue(data.getDisplayName());
                }
            });
        }
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

    public LiveData<DocumentSnapshot> getUserDocSnapshotLiveData() {
        if(userLiveData == null) {

            initUserMediatorData();
        }
        return userLiveData;
    }

    private void fetchUserLiveData(@NonNull String userID) {
            Timber.i("-->> UserData -->: >> beginning to retrieve data ");
            DocumentReference reference = firestore.collection("users").document(userID);
        changeUserLiveDataStatus(FETCHING);
            userLiveData = new FirestoreDocumentLiveData(reference, new FirestoreLiveDataListener() {
                @Override
                public void onEventSuccess(DocumentSnapshot documentSnapshot) {
                    Timber.e("UserData --> REPO --> Data Available reported");
                    changeUserLiveDataStatus(DATA_AVAILABLE);
                }

                @Override
                public void onEventFailure() {
                    changeUserLiveDataStatus(FAILED);
                }
            });
    }

    private void changeUserLiveDataStatus(@UserLiveDataStatusTypeDef int userLiveDataStatus) {
        setChanged();
        notifyObservers(userLiveDataStatus);
    }

    private UserData fetchStaticUserData(@NonNull String userID) {
        AtomicReference<UserData> reference = new AtomicReference<>();
        if(!userID.isEmpty()) {
            DocumentReference docRef = firestore.collection("users").document(userID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document!= null && document.exists()) {
                        Timber.d("-->> CatRepo: Firestore: DocumentSnapshot data: %s", document.getData());
                        reference.set(document.toObject(UserData.class));
                    } else {
                        Timber.e("-->> UserRepo:Firestore: No such document");
                    }
                } else {
                    Timber.e(task.getException(), "Task failed.");
                }
            });
        } else {
            Timber.e("UserID required to fetch UserData. Ensure UserManager has retrieved userID, and passed to getUserLiveData method.");
        }
        if(reference.get() != null) {
            return reference.get();
        } return null;
    }

    public void updateTotalCalories(String userID, String categoryName, int calorieChange) {
        if(calorieChange != 0) {
            appExecutors.networkIO().execute(() -> {
                UserData userData;
                if(getUserDataFromLiveData(userID) != null) {
                    // Most of the time, live snapshot data should be available to us, so we don't
                    // need to make another unnecessary read from the database.
                     userData = getUserDataFromLiveData(userID);
                } else {
                    // If live data wasn't available, we can make a static request
                    userData = fetchStaticUserData(userID);
                }
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
            UserData userData;
            if(getUserDataFromLiveData(userID) != null) {
                // Most of the time, live snapshot data should be available to us, so we don't
                // need to make another unnecessary read from the database.
                userData = getUserDataFromLiveData(userID);
            } else {
                // If live data wasn't available, we can make a static request
                userData = fetchStaticUserData(userID);
            }
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

    private void updateObserver(UserDataUpdateStatusObserver observer,
                                @UserDataUpdateStatusTypeDef int status) {
        if(observer != null) {
            observer.update(status);
        }
    }

}