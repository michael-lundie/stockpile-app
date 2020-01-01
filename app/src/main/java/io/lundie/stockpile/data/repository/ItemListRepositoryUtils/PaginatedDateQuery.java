package io.lundie.stockpile.data.repository.ItemListRepositoryUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Date;

import io.lundie.stockpile.data.model.ItemPile;
import timber.log.Timber;

import static io.lundie.stockpile.utils.DateUtils.localDateToDate;

public class PaginatedDateQuery {

    private PaginatedDateQueryListener listener;
    private CollectionReference reference;
    private int limitPerPage;
    private Date thresholdDate;
    private ArrayList<ItemPile> expiryList = new ArrayList<>();

    int tempCount =1;

    public interface PaginatedDateQueryListener {
        void onDataLoaded(ArrayList<ItemPile> expiringItemsArrayList);
        void onError(ArrayList<ItemPile> expiringItemsArrayList);
    }

    public PaginatedDateQuery(CollectionReference reference, int limitPerPage, int expiringWithinXMonths,
                              PaginatedDateQueryListener listener) {
        this.reference = reference;
        this.limitPerPage = limitPerPage;
        this.listener = listener;
        thresholdDate = localDateToDate(LocalDate.now().plusMonths(expiringWithinXMonths));
        first();
    }

    private void first() {
        Query firstQuery  = reference
                .orderBy("expiry", Query.Direction.DESCENDING)
                .limit(limitPerPage);
        processQuery(firstQuery);
    }

    private void next(DocumentSnapshot lastVisibleSnapshot) {
        Query nextQuery = reference
                .orderBy("expiry", Query.Direction.DESCENDING)
                .startAfter(lastVisibleSnapshot)
                .limit(limitPerPage);
        processQuery(nextQuery);
    }

    private void processQuery(Query query) {
        query.get()
                .addOnSuccessListener(documentSnapshots -> {
                    DocumentSnapshot lastVisibleSnapshot = convertSnapshotToItems(documentSnapshots);

                    if (lastVisibleSnapshot != null) {
                        next(lastVisibleSnapshot);
                    } else {
                        postDataToInterface(false);
                    }
                })
                .addOnFailureListener(error -> {
                    Timber.e(error, "Failure returning expiry items.");
                    postDataToInterface(true);
                });
    }

    private DocumentSnapshot convertSnapshotToItems(QuerySnapshot snapshots) {
        if(snapshots.getDocuments().size() > 0) {
            for (DocumentSnapshot snapshot : snapshots) {
                ItemPile itemPile = snapshot.toObject(ItemPile.class);
                if(itemPile != null && itemPile.getExpiry().size() > 0) {
                    if(itemPile.getExpiry().get(0).before(thresholdDate)) {
                        expiryList.add(itemPile);
                        Timber.e("Adding Item %s: %s,%s", tempCount, itemPile.getItemName(), itemPile.getExpiry().get(0));
                        tempCount++;
                    } else {
                        //We've reached the end of the list, return null here.
                        return null;
                    }
                }
            } return snapshots.getDocuments().get(snapshots.size() - 1);
        } return null;
    }

    private void postDataToInterface(boolean isErrorReported) {
        if(!isErrorReported) {
            Timber.i("Successfully posting data to interface. Items: %s", expiryList.size());
            listener.onDataLoaded(expiryList);
        } else {
            listener.onError(expiryList);
        }
    }
}