package io.lundie.stockpile.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.UserData;

public class UserRepository {

    private static final String LOG_TAG = UserRepository.class.getSimpleName();

    private FirebaseFirestore firestore;

    private MutableLiveData<String> testLiveData = new MutableLiveData<>();

    private MutableLiveData<String> homeLiveData = new MutableLiveData<>();
    private MutableLiveData<String> userDisplayName = new MutableLiveData<>();


    private MutableLiveData<UserData> userLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ItemCategory>> itemCategoryList = new MutableLiveData<>();

    @Inject
    UserRepository(FirebaseFirestore firebaseFirestore) {
        this.firestore = firebaseFirestore;
    }

    public LiveData<String> getHomeLiveData() {
        if(homeLiveData.getValue() == null) {
            homeLiveData.postValue("Test String");
        }

        return homeLiveData;
    }

    public MutableLiveData<String> getUserDisplayName() {
        if (userDisplayName.getValue() == null) {
            userDisplayName.postValue("Michael");
        }
        return userDisplayName;
    }

    public LiveData<String> getTestLiveData() {
        if(testLiveData.getValue() == null) {
            testLiveData.postValue("TEST LiveDATA");
        } return testLiveData;
    }

    public LiveData<UserData> getUserLiveData(@NonNull String userID) {
        if(userLiveData.getValue() == null) {
            fetchUserData(userID);
        } return userLiveData;
    }

    public MutableLiveData<ArrayList<ItemCategory>> getCategoryData() {

        Log.i(LOG_TAG, "-->> CatRepo: >> get Category Data called ");
        return itemCategoryList;
    }

    private void fetchUserData (@NonNull String userID) {
        Log.i(LOG_TAG, "-->> CatRepo: >> begging to retrieve data ");
        if(!userID.isEmpty()) {
            AtomicReference<UserData> reference = new AtomicReference<>();
            DocumentReference docRef = firestore.collection("users").document(userID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(LOG_TAG, "-->> CatRepo: Firestore: DocumentSnapshot data: " + document.getData());
                        reference.set(document.toObject(UserData.class));
                        userLiveData.setValue(document.toObject(UserData.class));
                        itemCategoryList.setValue(reference.get().getCategories());

                    } else {
                        Log.d(LOG_TAG, "-->> CatRepo:Firestore: No such document");
                    }
                } else {
                    Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
                }
            });
        } else {
            Log.e(LOG_TAG, "UserID required to fetch UserData. Ensure UserManager has" +
                    "retrieved userID, and passed to getUserLiveData method.");
        }

    }
}