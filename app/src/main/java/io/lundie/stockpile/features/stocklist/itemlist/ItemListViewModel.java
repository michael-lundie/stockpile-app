package io.lundie.stockpile.features.stocklist.itemlist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.AppExecutors;
import io.lundie.stockpile.utils.SingleLiveEvent;

public class ItemListViewModel extends FeaturesBaseViewModel {

    private static final String LOG_TAG = ItemListViewModel.class.getSimpleName();
    private final AppExecutors appExecutors;

    private final ItemListRepository itemListRepository;
    private String currentCategory = "";
    private String userID;

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

    @Override
    public void onAttemptingSignIn() {

    }

    @Override
    public void onSignInSuccess(String userID) {
        Log.e(LOG_TAG, "Extended model success - user ID: " + userID);
        this.userID = userID;
    }

    @Override
    public void onSignedInAnonymously(String userID) {

    }

    @Override
    public void onSignInFailed() {

    }


    public String getCurrentCategory() {
        return currentCategory;
    }

    void setCategory(String category) {
        if (!currentCategory.equals(category)) {
            itemListRepository.fetchListTypeItems(category, userID);
            addItemPileLiveDataSource();
        }
        this.currentCategory = category;
    }

    private void addItemPileLiveDataSource() {
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
            Log.e(LOG_TAG, "ItemListVM: snapshot is null!!");
        }
    }

    SingleLiveEvent<String> getAddItemNavEvent() {
        return addItemNavEvent;
    }

    public void onAddItemFabClicked() {
        addItemNavEvent.setValue(currentCategory);
        Log.e(LOG_TAG, "ITEM CLICKED: CAT ->" + currentCategory);
    }
}
