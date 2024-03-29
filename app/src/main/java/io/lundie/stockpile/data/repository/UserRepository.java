package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreDocumentLiveData;
import io.lundie.stockpile.data.FirestoreLiveDataListener;
import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.data.model.firestore.UserData;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusObserver;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType.UserDataUpdateStatusTypeDef;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import timber.log.Timber;

/**
 * User Repository is an {@link io.lundie.stockpile.injection.AppScope} scoped class, giving access
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

    private boolean isUserDataFetchInProgress = false;

    @Inject
    UserRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    /**
     * Initialises real-time user data updates.
     * @param userID ID of logged in user.
     */
    public void initUserDocumentRealTimeUpdates(@NonNull String userID) {
        if(userDocumentLiveData == null || userDocumentLiveData.getValue() == null
                && !isUserDataFetchInProgress) {
            Timber.i("UserData --> Getting user live data. UserID : %s", userID );
            beginUserDocumentRealTimeData(userID);
            initUserMediatorData();
        } else if (userDocumentLiveData.getValue() != null &&
                !userID.equals(userMediatorData.getValue().getUserID())) {
            boolean isNewID = !userID.equals(userMediatorData.getValue().getUserID());
            Timber.i("UserData --> new USER ID: %s", isNewID );
            beginUserDocumentRealTimeData(userID);
            initUserMediatorData();
        }
    }

    private void initUserMediatorData() {
        if (userMediatorData.getValue() == null) {
            userMediatorData.addSource(userDocumentLiveData, userDocument -> {
                if(userDocument.exists()) {
                    userMediatorData.setValue(userDocument.toObject(UserData.class));
                    Timber.e("UserData: UserDisplay is: %s", userMediatorData.getValue().getDisplayName());
                    Timber.e("UserData: UserRepoID: %s", System.identityHashCode(this));
                }
            });
        }
    }

    public LiveData<UserData> getUserMediatorData() {
        return userMediatorData;
    }

    public LiveData<DocumentSnapshot> getUserDocumentRealTimeData() { return userDocumentLiveData;  }

    /**
     * Method begins and manages the initialisation process of {@link FirestoreDocumentLiveData} of
     * our {@link UserData}.
     * @param userID ID of logged in user.
     */
    private void beginUserDocumentRealTimeData(@NonNull String userID) {
        Timber.e("UserData: beingRealTime data with ID: %s", userID);
        isUserDataFetchInProgress = true;
        userDocumentLiveData = new FirestoreDocumentLiveData(collectionPath(userID).document(userID),
                new FirestoreLiveDataListener() {
            @Override
            public void onEventSuccess(DocumentSnapshot documentSnapshot) {
                isUserDataFetchInProgress = false;
            }

            @Override
            public void onEventFailure() {
                isUserDataFetchInProgress = false;
            }
        });
    }

    /**
     * Method which fetches the most recent static version of {@link UserData} from firestore.
     * Used when we don't require LiveData updates of userdata.
     * @param userID ID of logged in user.
     * @return {@link UserData}
     */
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

    /**
     * Utility method used to update category item and calorie totals when an
     * {@link io.lundie.stockpile.data.model.firestore.ItemPile} is added or updated.
     * @param userID ID of currently logged in user.
     * @param categoryName category to update
     * @param calorieChange calorie change to add/remove from the category
     * @param itemPileCountChange item count to add/remove from the category
     */
    public void updateCategoryTotals(String userID, String categoryName, int calorieChange,
                                     int itemPileCountChange) {
        if(calorieChange != 0) {
            appExecutors.networkIO().execute(() -> {
                UserData userData = fetchMostRecentUserDocumentData(userID);
                if(userData != null) {
                    ArrayList<ItemCategory> categories = userData.getCategories();
                    for (int i = 0; i < categories.size(); i++) {
                        ItemCategory category = categories.get(i);
                        if(category.getCategoryName().equals(categoryName)) {
                            int totalCalories = category.getTotalCalories() + calorieChange;
                            int totalPiles = category.getNumberOfPiles() + itemPileCountChange;
                            category.setTotalCalories(totalCalories);
                            category.setNumberOfPiles(totalPiles);
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

    private void updateUserData(String userID, UserData userData, UserDataUpdateStatusObserver observer) {
        collectionPath(userID)
                .document(userID)
                .set(userData)
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

    @Override
    CollectionReference collectionPath(@NonNull String userID) {
        return firestore.collection("users");
    }

    private void stopObservers() {
        userMediatorData.removeSource(userDocumentLiveData);
    }

    public void clear() {
        stopObservers();
        userMediatorData.setValue(null);
        userDocumentLiveData = null;
        staticUserData = null;
    }
}