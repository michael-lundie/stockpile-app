package io.lundie.stockpile.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepositoryUtils.PaginatedDateQuery;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

import static io.lundie.stockpile.utils.DateUtils.getDatePlusXMonths;

public class ItemListRepository {

    private final FirebaseFirestore firestore;
    private final FirebaseStorage firebaseStorage;
    private final AppExecutors appExecutors;
    private PagingStatusListener pagingStatusListener;

    private FirestoreQueryLiveData itemsLiveData;
    private DocumentSnapshot lastVisibleExpiryPageSnapshot;
    private MutableLiveData<ArrayList<ItemPile>> pagingExpiryList = new MutableLiveData<>();

    private int pageLimit = 10;

    public interface PagingStatusListener {
        void onStop();
        void onError();
        void onLoaded();
    }

    @Inject
    ItemListRepository(FirebaseFirestore firebaseFirestore, FirebaseStorage firebaseStorage,
                       AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.firebaseStorage = firebaseStorage;
        this.appExecutors = appExecutors;
    }

    public void setPagingStatusListener(PagingStatusListener listener) {
        this.pagingStatusListener = listener;
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

    public LiveData<ArrayList<ItemPile>> getPagingExpiryListLiveData(@NonNull  String userID) {
        Timber.e("Paging --> Call received in repo");
        if(pagingExpiryList.getValue() == null) {
            getFirstExpiryListPage(userID);
        } return pagingExpiryList;
    }

    private void getFirstExpiryListPage(@NonNull  String userID) {
        CollectionReference itemsReference = firestore.collection("users").document(userID)
                .collection("items");
        Query firstQuery  = itemsReference
                .orderBy("expiry", Query.Direction.DESCENDING)
                .limit(pageLimit);
        processQuery(firstQuery);
        Timber.e("Paging --> Processing query FIRST");
    }

    public void getNextExpiryListPage(@NonNull  String userID) {
        CollectionReference itemsReference = firestore.collection("users").document(userID)
                .collection("items");
        Query nextQuery  = itemsReference
                .orderBy("expiry", Query.Direction.DESCENDING)
                .startAfter(lastVisibleExpiryPageSnapshot)
                .limit(pageLimit);
        processQuery(nextQuery);
        Timber.e("Paging --> Processing query NEXT");
    }

    private void processQuery(Query query) {
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Timber.e("Paging --> Setting up executor");
//                query.get()
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Timber.e("Paging --> Task is successful.");
//                                List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
//                                ItemListRepository.this.addPageToLiveData(ItemListRepository.this.convertSnapshotToItemPile(snapshots));
//                            } else if (task.getException() != null) {
//                                Timber.e(task.getException(), "Failure returning expiry items.");
//                                if (pagingStatusListener != null) {
//                                    pagingStatusListener.onError();
//                                }
//                            }
//                        });
//            }
//        }, 2000);
        appExecutors.networkIO().execute(() -> query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Timber.e("Paging --> Task is successful.");
                        List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                        addPageToLiveData(convertSnapshotToItemPile(snapshots));
                    } else if (task.getException() != null) {
                        Timber.e(task.getException(), "Failure returning expiry items.");
                        if (pagingStatusListener != null) {
                            pagingStatusListener.onError();
                        }
                    }
                }));
    }

    private ArrayList<ItemPile> convertSnapshotToItemPile(List<DocumentSnapshot> snapshots) {
        //TODO: Update using user preferences
        Date thresholdDate = getDatePlusXMonths(2);
        if(snapshots.size() > 0) {
            ArrayList<ItemPile> page = new ArrayList<>();
            for (DocumentSnapshot snapshot : snapshots) {
                ItemPile itemPile = snapshot.toObject(ItemPile.class);
                if(itemPile != null && itemPile.getExpiry().size() > 0) {
                    if(itemPile.getExpiry().get(0).before(thresholdDate)) {
                        page.add(itemPile);
//                        Timber.e("Adding Item %s: %s,%s", tempCount, itemPile.getItemName(), itemPile.getExpiry().get(0));
//                        tempCount++;
                    } else {
                        //We've reached the end of the list, return null here.
                        if(pagingStatusListener != null) {
                            pagingStatusListener.onStop();
                        }
                        break;
                    }
                }
            }
            if(snapshots.size() > 3) {
                lastVisibleExpiryPageSnapshot = snapshots.get(snapshots.size() - 2);
            } else {
                if(pagingStatusListener != null) {
                    pagingStatusListener.onStop();
                }
            }
            return  page;
        } return null;
    }

    private void addPageToLiveData(ArrayList<ItemPile> page) {
        ArrayList<ItemPile> currentList = pagingExpiryList.getValue();
        if(page != null) {
            if(currentList != null) {
                if(page.size() > (pageLimit -1)) {
                    page.set(page.size() -1, null);
                }
                currentList.remove(currentList.size() -1);
                currentList.addAll(page);
                pagingExpiryList.postValue(currentList);
            } else {
                if(page.size() > (pageLimit -1)) {
                    page.set(page.size() -1, null);
                }
                pagingExpiryList.postValue(page);
            }
            if(pagingStatusListener != null) {
                pagingStatusListener.onLoaded();
            }
        } else {
            if(pagingStatusListener != null) {
                pagingStatusListener.onStop();
            }
        }
        Timber.e("Paging --> Updating expiry list data.");
    }
}