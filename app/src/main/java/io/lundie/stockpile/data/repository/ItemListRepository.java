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

import io.lundie.stockpile.data.ItemList;
import io.lundie.stockpile.data.ListTypeItem;
import io.lundie.stockpile.data.UserData;
import io.lundie.stockpile.utils.data.FakeDataUtil;

public class ItemListRepository {

    private static final String LOG_TAG = ItemListRepository.class.getSimpleName();

    FirebaseFirestore firestore;

    private MutableLiveData<String> testLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ListTypeItem>> listTypeItemsMutableLD = new MutableLiveData<>();

    @Inject
    ItemListRepository(FirebaseFirestore firebaseFirestore) {
        this.firestore = firebaseFirestore;
    }

    public LiveData<String> getTestLiveData() {
        if(testLiveData.getValue() == null) {
            testLiveData.postValue("TEST LiveDATA");
        } return testLiveData;
    }

    public MutableLiveData<ArrayList<ListTypeItem>> getListTypeItemsMutableLD() {
        return listTypeItemsMutableLD;
    }

    public void fetchListTypeItems (String categoryName) {
        Log.i(LOG_TAG, "-->> ItemListRepo: >> begging to retrieve data ");
        AtomicReference<ItemList> reference = new AtomicReference<>();

        DocumentReference docRef = firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
                .collection("category").document(categoryName);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(LOG_TAG, "-->> ItemListRepo: Firestore: DocumentSnapshot data: " + document.getData());
                    reference.set(document.toObject(ItemList.class));
                    listTypeItemsMutableLD.setValue(reference.get().getListItems());
                    //Log.i(LOG_TAG, "-->> CatRepo: Firestore result: " + userData.getCategories());

                } else {
                    Log.d(LOG_TAG, "-->> ItemListRepo:Firestore: No such document");
                }
            } else {
                Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
            }
        });
    }
}
