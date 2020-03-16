package io.lundie.stockpile.features.stocklist.item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.TransactionUpdateIdType;
import timber.log.Timber;

public class ItemViewModel extends FeaturesBaseViewModel {

    private final ItemRepository itemRepository;

    private MediatorLiveData<ItemPile> itemLiveData = new MediatorLiveData<>();
    private LiveData<String> itemName;
    private LiveData<String> itemCategory;
    private LiveData<String> imagePath;
    private LiveData<String> pileTotalItems;
    private LiveData<String> pileTotalCalories;
    private LiveData<String> itemCalories;
    private LiveData<ArrayList<Date>> pileExpiryList;

    @Inject
    ItemViewModel(@NonNull Application application, ItemRepository itemRepository) {
        super(application);
        this.itemRepository = itemRepository;
    }

    public void setItem(String itemName) {
        Timber.e("Setting item view model %s", itemName);
        fetchItemLiveData(itemName);
        initTransformations();
    }

    private void fetchItemLiveData(String itemName) {
        itemLiveData.addSource(itemRepository.getItemLiveDataSnapshot(getUserID(), itemName), snapshot -> {
            ItemPile itemPile = snapshot.toObject(ItemPile.class);
            itemLiveData.setValue(itemPile);
        });
    }

    void onDeleteClicked() {
        String itemPileName = itemName.getValue();
        getStatusController().createEventPacket(
                TransactionUpdateIdType.ITEM_UPDATE_ID,
                getApplication().getResources().getString(R.string.events_msg_item_deleted),
                null);
        itemRepository.deleteItemPile(getUserID(), itemPileName);
    }

    private void initTransformations() {
        this.itemName = Transformations.map(itemLiveData, ItemPile::getItemName);
        this.itemCategory = Transformations.map(itemLiveData, ItemPile::getCategoryName);
        this.imagePath = Transformations.map(itemLiveData, ItemPile::getImagePath);
        this.pileTotalItems = Transformations.map(itemLiveData, itemPile ->
                String.valueOf(itemPile.getItemCount()));
        this.pileTotalCalories = Transformations.map(itemLiveData, itemPile ->
                String.valueOf(itemPile.getItemCount() * itemPile.getCalories()));
        this.imagePath = Transformations.map(itemLiveData, ItemPile::getImagePath);
        this.itemCalories = Transformations.map(itemLiveData, itemPile ->
                String.valueOf(itemPile.getCalories()));
        this.pileExpiryList = Transformations.map(itemLiveData, ItemPile::getExpiry);
    }

    public LiveData<String> getImagePath() { return imagePath; }

    public LiveData<String> getItemName() {
        return itemName;
    }

    public LiveData<String> getItemCategory() { return itemCategory; }

    public LiveData<String> getPileTotalItems() { return pileTotalItems; }

    public LiveData<String> getPileTotalCalories() { return pileTotalCalories; }

    public LiveData<ArrayList<Date>> getPileExpiryList() { return pileExpiryList; }

    void setItemPileBus() {
        if(itemLiveData != null && itemLiveData.getValue() != null) {
            getItemPileBus().setItemPile(itemLiveData.getValue());
        }
    }
}