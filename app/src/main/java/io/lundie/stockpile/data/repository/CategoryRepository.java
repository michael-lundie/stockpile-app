package io.lundie.stockpile.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.ItemCategory;
import io.lundie.stockpile.data.UserData;
import io.lundie.stockpile.utils.data.FakeDataUtil;

public class CategoryRepository {

    private static final String LOG_TAG = CategoryRepository.class.getSimpleName();

    private MutableLiveData<String> testLiveData = new MutableLiveData<>();

    private MutableLiveData<UserData> liveUserData = new MutableLiveData<>();

    private MutableLiveData<ArrayList<ItemCategory>> itemCategoryList = new MutableLiveData<>();

    FirebaseFirestore firestore;

    @Inject
    CategoryRepository(FirebaseFirestore firebaseFirestore) {
        this.firestore = firebaseFirestore;
        Log.i(LOG_TAG, "-->> CatRepo: initialising. <<--");
        if(liveUserData.getValue() == null) {
            getUserData();
        }
    }

    public LiveData<String> getTestLiveData() {
        if(testLiveData.getValue() == null) {
            testLiveData.postValue("TEST LiveDATA");
        } return testLiveData;
    }

    public MutableLiveData<ArrayList<ItemCategory>> getCategoryData() {

        Log.i(LOG_TAG, "-->> CatRepo: >> value set for categories ");
        return itemCategoryList;
    }

    private void getUserData () {
        Log.i(LOG_TAG, "-->> CatRepo: >> begging to retrieve data ");
        AtomicReference<UserData> reference = new AtomicReference<>();

        DocumentReference docRef = firestore.collection("users").document(FakeDataUtil.TEST_USER_ID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(LOG_TAG, "-->> CatRepo: Firestore: DocumentSnapshot data: " + document.getData());
                    reference.set(document.toObject(UserData.class));
                    liveUserData.setValue(reference.get());
                    itemCategoryList.setValue(reference.get().getCategories());
                    //Log.i(LOG_TAG, "-->> CatRepo: Firestore result: " + userData.getCategories());

                } else {
                    Log.d(LOG_TAG, "-->> CatRepo:Firestore: No such document");
                }
            } else {
                Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
            }
        });
    }
}