package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;

public class ItemListRepository {

    private static final String LOG_TAG = ItemListRepository.class.getSimpleName();

    private final FirebaseFirestore firestore;
    private final FirebaseStorage firebaseStorage;

    private FirestoreQueryLiveData itemsLiveData;

    @Inject
    ItemListRepository(FirebaseFirestore firebaseFirestore, FirebaseStorage firebaseStorage) {
        this.firestore = firebaseFirestore;
        this.firebaseStorage = firebaseStorage;
    }

    public LiveData<QuerySnapshot> getItemsQuerySnapshotLiveData() {
        return itemsLiveData;
    }


    public void fetchListTypeItems (@NonNull String categoryName, @NonNull String userID) {
        CollectionReference itemsReference = firestore.collection("users").document(userID)
                .collection("items");
        Query itemsQuery  = itemsReference.whereEqualTo("categoryName", categoryName);
        //TODO: Requires Index
                //.orderBy("itemName", Query.Direction.ASCENDING);
        itemsLiveData = new FirestoreQueryLiveData(itemsQuery);


//        Log.i(LOG_TAG, "-->> ItemListRepo: >> begging to retrieve data ");
//        AtomicReference<ItemList> reference = new AtomicReference<>();
//
//        DocumentReference docRef = firestore.collection("users").document(userID)
//                .collection("category").document(categoryName);
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Log.d(LOG_TAG, "-->> ItemListRepo: Firestore: DocumentSnapshot data: " + document.getData());
//                    reference.set(document.toObject(ItemList.class));
//                    listTypeItemsMutableLD.setValue(reference.get().getListItems());
//                    //Log.i(LOG_TAG, "-->> CatRepo: Firestore result: " + userData.getCategories());
//
//                } else {
//                    Log.d(LOG_TAG, "-->> ItemListRepo:Firestore: No such document");
//                }
//            } else {
//                Log.d(LOG_TAG, "Firestore: get failed with ", task.getException());
//            }
//        });
    }
}
