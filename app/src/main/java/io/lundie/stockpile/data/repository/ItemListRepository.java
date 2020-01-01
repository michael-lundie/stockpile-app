package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepositoryUtils.PaginatedDateQuery;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

public class ItemListRepository {

    private final FirebaseFirestore firestore;
    private final FirebaseStorage firebaseStorage;
    private final AppExecutors appExecutors;

    private FirestoreQueryLiveData itemsLiveData;
    private MutableLiveData<ArrayList<ItemPile>> expiryListMutableData = new MutableLiveData<>();

    @Inject
    ItemListRepository(FirebaseFirestore firebaseFirestore, FirebaseStorage firebaseStorage,
                       AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.firebaseStorage = firebaseStorage;
        this.appExecutors = appExecutors;
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
    }

    public LiveData<ArrayList<ItemPile>> getExpiryListLiveData() {
        return expiryListMutableData;
    }

    public void fetchExpiringItems(@NonNull String userID, int itemsToReturn) {

        Timber.e("UserID is: %s", userID);

        CollectionReference itemsReference = firestore.collection("users").document(userID)
                .collection("items");

        appExecutors.networkIO().execute(() -> {
            new PaginatedDateQuery(itemsReference, 10, 1,
                    new PaginatedDateQuery.PaginatedDateQueryListener() {
                        @Override
                        public void onDataLoaded(ArrayList<ItemPile> expiringItemsArrayList) {
                            expiryListMutableData.postValue(expiringItemsArrayList);
                            for (ItemPile item : expiringItemsArrayList
                                 ) {
                                Timber.i("Expiry Entry: %s, %s", item.getItemName(), item.getExpiry().get(0));

                            }
                        }

                        @Override
                        public void onError(ArrayList<ItemPile> expiringItemsArrayList) {
                            Timber.e("Expiry Entry: There was an error.");
                        }
                    });
        });
    }
}
