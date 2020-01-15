package io.lundie.stockpile.features.stocklist.item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import timber.log.Timber;

public class ItemViewModel extends FeaturesBaseViewModel {

    private final ItemRepository itemRepository;
    private MediatorLiveData<ItemPile> itemPileLiveData = new MediatorLiveData<>();
    private String currentItemName = "";

    private MutableLiveData<String> itemName = new MutableLiveData<>();
    private MutableLiveData<String> itemCategory = new MutableLiveData<>();
    private MutableLiveData<String> imageUri = new MutableLiveData<>();
    private MutableLiveData<String> pileTotalItems = new MutableLiveData<>();
    private MutableLiveData<String> pileTotalCalories = new MutableLiveData<>();
    private MutableLiveData<String> itemCalories = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Date>> pileExpiryList = new MutableLiveData<>();

    @Inject
    ItemViewModel(@NonNull Application application, ItemRepository itemRepository) {
        super(application);
        this.itemRepository = itemRepository;
    }

    @Override
    public void onItemPileBusInjected(ItemPileBus itemPileBus) {
        ItemPile itemPile = getItemPileBus().getItemPile();
        int caloriesPerItem = itemPile.getCalories();
        int totalItems = itemPile.getItemCount();

        itemName.setValue(itemPile.getItemName());
        itemCategory.setValue(itemPile.getCategoryName());
        imageUri.setValue(itemPile.getImageURI());
        pileTotalItems.setValue(String.valueOf(totalItems));
        pileTotalCalories.setValue(String.valueOf(caloriesPerItem * totalItems));
        itemCalories.setValue(String.valueOf(caloriesPerItem));
        pileExpiryList.setValue(itemPile.getExpiry());
        if(itemPile.getExpiry() != null) {
            Timber.i("Adapter Items: %s", itemPile.getExpiry().size() );
        } else {

            Timber.i("EXPIRY LIST IS NULL!!!" );
        }

    }

//    void setItem(String itemName) {
//        if (!currentItemName.equals(itemName)) {
//            itemRepository.fetchItemPile(getUserID(), itemName);
//            addItemPileLiveDataSource();
//        }
//        this.currentItemName = itemName;
//    }

//    private void addItemPileLiveDataSource() {
//        if(getItemDocumentSnapshot() != null) {
//            itemPileLiveData.addSource(getItemDocumentSnapshot(), documentSnapshot -> {
//                if(documentSnapshot != null) {
//                    itemPileLiveData.setValue(documentSnapshot.toObject(ItemPile.class));
//
//                    itemName.setValue(itemPileLiveData.getValue().getItemName());
//                    Timber.e("Item name is %s", itemPileLiveData.getValue().getItemName());
//                    imageUri.setValue(itemPileLiveData.getValue().getImageURI());
//                    Timber.e("Item uri is %s", itemPileLiveData.getValue().getImageURI());
//                } else {
//                    itemPileLiveData.setValue(null);
//                    Timber.e("Document Snapshot is null.");
//                }
//            });
//        } else {
//            Timber.e("Document Snapshot is null.");
//        }
//    }

    public LiveData<String> getImageURI() {
        return imageUri;
    }

    public LiveData<String> getItemName() {
        return itemName;
    }

    public LiveData<String> getItemCategory() { return itemCategory; }

    public LiveData<String> getPileTotalItems() { return pileTotalItems; }

    public LiveData<String> getPileTotalCalories() { return pileTotalCalories; }

    public LiveData<ArrayList<Date>> getPileExpiryList() { return pileExpiryList; }
}