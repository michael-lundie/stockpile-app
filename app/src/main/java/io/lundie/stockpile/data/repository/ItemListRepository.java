package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.WireFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.utils.threadpool.AppExecutors;

import static io.lundie.stockpile.utils.DateUtils.getDatePlusXMonths;

/**
 * Repository responsible for the retrieval of multiple {@link ItemPile} data in list format,
 * stored in firestore cloud. This repository functions as a "one true source" access point for data.
 *
 * Note that because this repository has {@link io.lundie.stockpile.injection.AppScope} it must be
 * manually cleared on user sign-out. This class and it's data is not guaranteed to be destroyed
 * on sign-out.
 */
public class ItemListRepository extends BaseRepository{

    private final FirebaseFirestore firestore;
    private final AppExecutors appExecutors;
    private PagingStatusListener pagingStatusListener;

    private FirestoreQueryLiveData itemsLiveData;
    private DocumentSnapshot lastVisibleExpiryPageSnapshot;
    private MutableLiveData<ArrayList<ItemPile>> pagingExpiryList = new MutableLiveData<>();
    private ArrayList<ItemPile> expiringItemsWidgetList;
    private int pageLimit = 10;

    @Inject
    ItemListRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public void setPagingStatusListener(PagingStatusListener listener) {
        this.pagingStatusListener = listener;
    }

    public LiveData<QuerySnapshot> getItemsQuerySnapshotLiveData() {
        return itemsLiveData;
    }

    /**
     * Method creates a new {@link FirestoreQueryLiveData}, returning items within the requested
     * category.
     *
     * @param categoryName retrieve {@link ItemPile}s which are in this category.
     * @param userID       UserID of logged in user.
     */
    public void fetchListTypeItems(@NonNull String categoryName, @NonNull String userID) {
        //TODO: Requires Index
        //.orderBy("itemName", Query.Direction.ASCENDING);
        itemsLiveData = new FirestoreQueryLiveData(collectionPath(userID)
                .whereEqualTo("categoryName", categoryName));
    }

    public void getExpiringItemsWidgetList(String userID, WidgetListener listener) {
        ArrayList<ItemPile> pagedExpiryList = pagingExpiryList.getValue();
        expiringItemsWidgetList = new ArrayList<>();
        if(pagedExpiryList != null && !pagedExpiryList.isEmpty()) {
            for (int i = 0; i < pagedExpiryList.size(); i++) {
                expiringItemsWidgetList.add(pagedExpiryList.get(i));
                if(i >= 9) { break; }
            }
            listener.onComplete(expiringItemsWidgetList);
        } else {
            fetchExpiringItemsWidgetList(userID, listener);
        }
    }

    private void fetchExpiringItemsWidgetList(String userID, WidgetListener listener) {
        Query query = collectionPath(userID)
                .orderBy("expiry", Query.Direction.DESCENDING)
                .limit(pageLimit);

        appExecutors.networkIO().execute(() -> query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                        expiringItemsWidgetList = new ArrayList<>();
                        for (DocumentSnapshot snapshot : snapshots) {
                            ItemPile itemPile = snapshot.toObject(ItemPile.class);
                            expiringItemsWidgetList.add(itemPile);
                        }
                        listener.onComplete(expiringItemsWidgetList);
                    } else if (task.getException() != null) {
                        listener.onFail();
                    }
                }));
    }

    /**
     * Begins a paging query for expiring {@link ItemPile} items and returns a LiveData wrapped
     * ArrayList. The LiveData will be subsequently updated on method calls to the
     * {@link #fetchNextExpiryListPage(String)} method.
     *
     * @param userID UserID of logged in user.
     * @return {@link MutableLiveData} wrapped ArrayList of {@link ItemPile} data.
     */
    public MutableLiveData<ArrayList<ItemPile>> getPagingExpiryListLiveData(@NonNull String userID) {
        if (pagingExpiryList.getValue() == null || pagingExpiryList.getValue().isEmpty()) {
            fetchFirstExpiryListPage(userID);
        }
        return pagingExpiryList;
    }

    /**
     * Fetches the first 'page' of expiring {@link ItemPile} data
     *
     * @param userID UserID of logged in user.
     */
    private void fetchFirstExpiryListPage(@NonNull String userID) {
        Query firstQuery = collectionPath(userID)
                .orderBy("expiry", Query.Direction.DESCENDING)
                .limit(pageLimit);
        processPagedQuery(firstQuery , true);
    }

    /**
     * fetches subsequent 'pages' of {@link ItemPile} data.
     * The first fetch should be made by the {@link #fetchFirstExpiryListPage(String)} method.
     *
     * @param userID UserID of logged in user.
     */
    public void fetchNextExpiryListPage(@NonNull String userID) {
        Query nextQuery = collectionPath(userID)
                .orderBy("expiry", Query.Direction.DESCENDING)
                .startAfter(lastVisibleExpiryPageSnapshot)
                .limit(pageLimit);
        processPagedQuery(nextQuery, false);
    }

    /**
     * Creates a new thread, requests and processes a firestore query.
     * This method is for use with item list paging methods.
     *
     * @param query {@link Query}
     */
    private void processPagedQuery(Query query, boolean isFirstQuery) {
        appExecutors.networkIO().execute(() -> query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                        ArrayList<ItemPile> firstQueryPile = convertPagedSnapshotToItemPile(snapshots);
                        addPageToLiveData(firstQueryPile);
                        // Add to widget list to save unnecessary queries to firestore
                        if(isFirstQuery) { expiringItemsWidgetList = firstQueryPile; }
                    } else if (task.getException() != null) {
                        if (pagingStatusListener != null) {
                            pagingStatusListener.onError();
                        }
                    }
                }));
    }

    /**
     * Utility method for use with other paging methods. Converts a List of {@link DocumentSnapshot}
     * data to an array list of {@link ItemPile} data. Sends a stop message to
     * {@link PagingStatusListener} when there are no items left in a snapshot.
     *
     * @param snapshots
     * @return
     */
    private ArrayList<ItemPile> convertPagedSnapshotToItemPile(List<DocumentSnapshot> snapshots) {
        //TODO: Update using user preferences
        Date thresholdDate = getDatePlusXMonths(2);
        if (snapshots.size() > 0) {
            ArrayList<ItemPile> page = new ArrayList<>();
            for (DocumentSnapshot snapshot : snapshots) {
                ItemPile itemPile = snapshot.toObject(ItemPile.class);
                if (itemPile != null && itemPile.getExpiry().size() > 0) {
                    if (itemPile.getExpiry().get(0).before(thresholdDate)) {
                        page.add(itemPile);
                    } else {
                        //We've reached the end of the list, return null here.
                        if (pagingStatusListener != null) {
                            pagingStatusListener.onStop();
                        }
                        break;
                    }
                }
            }
            if (snapshots.size() > 3) {
                lastVisibleExpiryPageSnapshot = snapshots.get(snapshots.size() - 2);
            } else {
                if (pagingStatusListener != null) {
                    pagingStatusListener.onStop();
                }
            }
            return page;
        }
        return null;
    }

    /**
     * Adds a page of (an array list of {@link ItemPile} data set at a specific limit) to our paged
     * live data ({@link #pagingExpiryList}. Informs a {@link PagingStatusListener} of data status.
     * there are no items left in a snapshot.
     * @param page
     */
    private void addPageToLiveData(ArrayList<ItemPile> page) {
        ArrayList<ItemPile> currentList = pagingExpiryList.getValue();
        if (page != null) {
            if (currentList != null && currentList.size() > 0) {
                if (page.size() > (pageLimit - 1)) {
                    page.set(page.size() - 1, null);
                }
                currentList.remove(currentList.size() - 1);
                currentList.addAll(page);
                pagingExpiryList.postValue(currentList);
            } else {
                if (page.size() > (pageLimit - 1)) {
                    page.set(page.size() - 1, null);
                }
                pagingExpiryList.postValue(page);
            }
            if (pagingStatusListener != null) {
                pagingStatusListener.onLoaded();
            }
        } else {
            if (pagingStatusListener != null) {
                pagingStatusListener.onStop();
            }
        }
    }

    /**
     * Current collection path of items data (for list queries). All query methods should use
     * this method to access the correct collection path. Updating ItemPile data should be done
     * via the {@link ItemRepository}.
     *
     * @param userID User ID of logged in user.
     * @return {@link CollectionReference}
     */
    @Override
    CollectionReference collectionPath(@NonNull String userID) {
        return firestore.collection("users")
                .document(userID)
                .collection("items");
    }

    public interface WidgetListener {
        void onComplete(ArrayList<ItemPile> itemPiles);
        void onFail();
    }

    /**
     * Interface for use with paging query methods.
     */
    public interface PagingStatusListener {
        void onStop();
        void onError();
        void onLoaded();
    }

    public void clear() {
        pagingExpiryList.setValue(new ArrayList<>());
        expiringItemsWidgetList = null;
    }
}