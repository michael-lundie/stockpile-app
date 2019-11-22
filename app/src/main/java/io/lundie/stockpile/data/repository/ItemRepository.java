package io.lundie.stockpile.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

public class ItemRepository {

    private static final String LOG_TAG = ItemRepository.class.getSimpleName();

    MutableLiveData<String> testLiveData = new MutableLiveData<>();

    FirebaseFirestore firestore;

    @Inject
    ItemRepository(FirebaseFirestore firebaseFirestore) {
        this.firestore = firebaseFirestore;
        Log.i(LOG_TAG, "-->> ItemRepository: initialising. <<--");
    }

    public LiveData<String> getTestLiveData() {
        if(testLiveData != null) {
            testLiveData.setValue("TEST LIVE DATA");
        }
        return testLiveData;
    }
}
