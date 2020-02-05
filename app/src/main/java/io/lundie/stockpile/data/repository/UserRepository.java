package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreDocumentLiveData;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

/**
 * TODO: We need to do null checks in our repo, to re-fetch data if null. Implement this across view models.
 * User Repository is an {@link io.lundie.stockpile.injection.AppScope} class, giving access
 * to user data fetched from FireStore.
 * IMPORTANT: Since user repository has application scope, be very careful when adding
 * {@link MediatorLiveData} sources. Make sure an observer will not be added multiple times.
 */
public class UserRepository {

    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;

    private MediatorLiveData<String> userDisplayName = new MediatorLiveData<>();

    private FirestoreDocumentLiveData userLiveData;

    //private MutableLiveData<UserData> userLiveData = new MutableLiveData<>();
    private MediatorLiveData<ArrayList<ItemCategory>> itemCategoryList = new MediatorLiveData<>();

    @Inject
    UserRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public MutableLiveData<String> getUserDisplayName() {
        return userDisplayName;
    }

    public UserData getUserDataSnapshot(@NonNull String userID) {
        if(userLiveData == null || userLiveData.getValue() == null) {
            Timber.i("UserData --> Getting user live data. UserID : %s", userID );
            fetchUserLiveData(userID);
            initMediatorData();
        } else {
            return userLiveData.getValue().toObject(UserData.class);
        } return null;
    }

    private void initMediatorData() {
        userDisplayName.addSource(userLiveData, snapshot -> {
            UserData data = snapshot.toObject(UserData.class);
            if(data != null) {
                userDisplayName.setValue(data.getDisplayName());
            }
        });
    }

    public LiveData<ArrayList<ItemCategory>> getCategoryData() {
        Timber.e("#UserData --> --> Repo Getter: Category List is setting as: %s", itemCategoryList.getValue());
        return itemCategoryList;
    }

    public LiveData<DocumentSnapshot> getUserDocSnapshotLiveData() {
        return userLiveData;
    }

    private void fetchUserLiveData(@NonNull String userID) {
            Timber.i("-->> UserData -->: >> beginning to retrieve data ");
            DocumentReference reference = firestore.collection("users").document(userID);
            userLiveData = new FirestoreDocumentLiveData(reference);
    }

    private UserData fetchUserData(@NonNull String userID) {
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
                if(getUserDataSnapshot(userID) != null) {
                    // Most of the time, live snapshot data should be available to us, so we don't
                    // need to make another unnecessary read from the database.
                     userData = getUserDataSnapshot(userID);
                } else {
                    // If live data wasn't available, we can make a static request
                    userData = fetchUserData(userID);
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
                            updateUserData(userID, userData);
                            break;
                        }
                    }
                }
            });
        }
    }

    private void updateUserData(String userID, UserData userData) {
        DocumentReference documentReference = firestore.collection("users")
                .document(userID);
        documentReference.set(userData)
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Timber.i("UserData --> Successfully updated user data.");
            } else {
                Timber.e(task.getException(), "UserData --> Error updating document.");
            }
        });
    }
}