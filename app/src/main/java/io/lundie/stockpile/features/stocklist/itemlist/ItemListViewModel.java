package io.lundie.stockpile.features.stocklist.itemlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import io.lundie.stockpile.utils.SingleLiveEvent;
import timber.log.Timber;

public class ItemListViewModel extends FeaturesBaseViewModel {

    private final AppExecutors appExecutors;

    private final ItemListRepository itemListRepository;
    private String currentCategory = "";

    private final MediatorLiveData<ArrayList<ItemPile>> itemPilesLiveData = new MediatorLiveData<>();
    private SingleLiveEvent<String> addItemNavEvent = new SingleLiveEvent<>();

    @Inject
    ItemListViewModel(@NonNull Application application, ItemListRepository itemListRepository,
                      AppExecutors appExecutors) {
        super(application);
        this.itemListRepository = itemListRepository;
        this.appExecutors = appExecutors;
    }

    LiveData<ArrayList<ItemPile>> getItemPilesLiveData() {
        return itemPilesLiveData;
    }

    private LiveData<QuerySnapshot> getItemsQuerySnapshot() {
        return itemListRepository.getItemsQuerySnapshotLiveData();
    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    void setCategory(String category) {
        if (!currentCategory.equals(category)) {
            itemListRepository.fetchListTypeItems(category, getUserID());
            addItemPileCollectionLiveDataSource();
        }
        this.currentCategory = category;
    }

    private void addItemPileCollectionLiveDataSource() {
        if(getItemsQuerySnapshot() != null) {
            itemPilesLiveData.addSource(getItemsQuerySnapshot(), queryDocumentSnapshots -> {
                if(queryDocumentSnapshots != null) {
                    appExecutors.diskIO().execute(() -> {
                        ArrayList<ItemPile> itemPiles = new ArrayList<>();
                        for(DocumentSnapshot document : queryDocumentSnapshots) {
                            ItemPile itemPile = document.toObject(ItemPile.class);
                            itemPiles.add(itemPile);
                        }
                        itemPilesLiveData.postValue(itemPiles);
                    });
                } else {
                    itemPilesLiveData.setValue(null);
                }
            });
        } else {
            Timber.e("Query Snapshot is null.");
        }
    }

    ItemPile getItemWithName(String itemName) {
        if(itemPilesLiveData.getValue() != null) {
            for (ItemPile item : itemPilesLiveData.getValue()) {
                if (item.getItemName().equals(itemName)) {
                    Timber.e("RETURNING ITEM WITH NAME --> %s", itemName);
                    return item;
                }
            }
        } return null;
    }

    SingleLiveEvent<String> getAddItemNavEvent() {
        return addItemNavEvent;
    }

    public void onAddItemFabClicked() {
        addItemNavEvent.setValue(currentCategory);
    }
}