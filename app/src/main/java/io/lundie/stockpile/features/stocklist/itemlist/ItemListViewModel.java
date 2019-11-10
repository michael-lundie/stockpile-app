package io.lundie.stockpile.features.stocklist.itemlist;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.utils.AppExecutors;
public class ItemListViewModel extends ViewModel {

    private static final String LOG_TAG = ItemListViewModel.class.getSimpleName();
    private final AppExecutors appExecutors;

    private final ItemListRepository itemListRepository;
    private String currentCategory = "";

    private final MediatorLiveData<ArrayList<ItemPile>> itemPilesLiveData = new MediatorLiveData<>();

    @Inject
    ItemListViewModel(ItemListRepository itemListRepository,
                      AppExecutors appExecutors) {
        this.itemListRepository = itemListRepository;
        this.appExecutors = appExecutors;

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

        //itemPilesLiveData = Transformations.map(getItemsQuerySnapshot(), new ItemPilesDeserializer());
    }

    LiveData<ArrayList<ItemPile>> getItemPilesLiveData() {
        return itemPilesLiveData;
    }

    private LiveData<QuerySnapshot> getItemsQuerySnapshot() {
        return itemListRepository.getItemsQuerySnapshotLiveData();
    }


    //TODO: Inject desericalizer through dagger
    private class ItemPilesDeserializer implements Function<QuerySnapshot, ArrayList<ItemPile>> {
        @Override
        public ArrayList<ItemPile> apply(QuerySnapshot queryDocumentSnapshots) {
            ArrayList<ItemPile> itemPiles = new ArrayList<>();
            for(DocumentSnapshot document : queryDocumentSnapshots) {
                ItemPile itemPile = document.toObject(ItemPile.class);
                itemPiles.add(itemPile);
            }
            return itemPiles;
        }
    }


    void setCategory(String category) {
        if (!currentCategory.equals(category)) {
            itemListRepository.fetchListTypeItems(category);
        }
        this.currentCategory = category;
    }

//    /**
//     * Returns the picasso instance from the view model for use in adapter classed.
//     * (Currently unable to be injected, due to the se of nav controller.)
//     * //TODO: Find a better way to inject picasso into the data binding layer.
//     * @return Picasso instance.
//     */
//    public Picasso getPicasso() {
//        if(picasso != null) {
//            return picasso;
//        } else {
//            Log.e(LOG_TAG, "Picasso Instance was null. Check VIEW-MODEL instantiation");
//            return null;
//        }
//    }
}
