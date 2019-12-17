package io.lundie.stockpile.features.stocklist.item;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import timber.log.Timber;

public class ItemViewModel extends FeaturesBaseViewModel {

    private final ItemRepository itemRepository;
    private MediatorLiveData<ItemPile> itemPileLiveData = new MediatorLiveData<>();
    private String currentItemName = "";

    private MutableLiveData<String> itemName = new MutableLiveData<>();
    private MutableLiveData<String> imageUri = new MutableLiveData<>();

    @Inject
    ItemViewModel(@NonNull Application application, ItemRepository itemRepository) {
        super(application);
        this.itemRepository = itemRepository;
    }

    void setItem(String itemName) {
        if (!currentItemName.equals(itemName)) {
            itemRepository.fetchItemPile(getUserID(), itemName);
            addItemPileLiveDataSource();
        }
        this.currentItemName = itemName;
    }

    private void addItemPileLiveDataSource() {
        if(getItemDocumentSnapshot() != null) {
            itemPileLiveData.addSource(getItemDocumentSnapshot(), documentSnapshot -> {
                if(documentSnapshot != null) {
                    itemPileLiveData.setValue(documentSnapshot.toObject(ItemPile.class));

                    itemName.setValue(itemPileLiveData.getValue().getItemName());
                    Timber.e("Item name is %s", itemPileLiveData.getValue().getItemName());
                    imageUri.setValue(itemPileLiveData.getValue().getImageURI());
                    Timber.e("Item uri is %s", itemPileLiveData.getValue().getImageURI());
                } else {
                    itemPileLiveData.setValue(null);
                    Timber.e("Document Snapshot is null.");
                }
            });
        } else {
            Timber.e("Document Snapshot is null.");
        }
    }

    private LiveData<DocumentSnapshot> getItemDocumentSnapshot() {
        return itemRepository.getItemDocumentSnapshotLiveData();
    }

    public LiveData<String> getImageURI() {
        return imageUri;
    }

    public LiveData<String> getItemName() {
        return itemName;
    }
}
