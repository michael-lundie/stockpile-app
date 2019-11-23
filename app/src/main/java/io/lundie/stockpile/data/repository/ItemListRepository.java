package io.lundie.stockpile.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.data.model.ItemList;
import io.lundie.stockpile.data.model.ListTypeItem;
import io.lundie.stockpile.utils.data.FakeDataUtil;

public class ItemListRepository {

    private static final String LOG_TAG = ItemListRepository.class.getSimpleName();

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    private MutableLiveData<ArrayList<ListTypeItem>> listTypeItemsMutableLD = new MutableLiveData<>();

    CollectionReference itemsReference;
    Query itemsQuery;

    private final FirestoreQueryLiveData itemsLiveData;

    @Inject
    ItemListRepository(FirebaseFirestore firebaseFirestore, FirebaseStorage firebaseStorage) {
        this.firestore = firebaseFirestore;
        this.firebaseStorage = firebaseStorage;
        itemsReference = firestore.collection("users").document(FakeDataUtil.TEST_USER_ID)
                .collection("items");
        itemsQuery  = itemsReference.orderBy("itemName", Query.Direction.ASCENDING);
        itemsLiveData = new FirestoreQueryLiveData(itemsQuery);
    }

    public LiveData<QuerySnapshot> getItemsQuerySnapshotLiveData() {
        return itemsLiveData;
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
