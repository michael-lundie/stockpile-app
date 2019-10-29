package io.lundie.stockpile.features.stocklist.itemlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.ListTypeItem;
import io.lundie.stockpile.data.repository.ItemListRepository;

public class ItemListViewModel extends ViewModel {

    private static final String LOG_TAG = ItemListViewModel.class.getSimpleName();

    private ItemListRepository itemListRepository;
    private String currentCategory = "";

    @Inject
    ItemListViewModel(ItemListRepository itemListRepository) {
        this.itemListRepository = itemListRepository;
    }

    public LiveData<ArrayList<ListTypeItem>> getListTypeItems() {
        return itemListRepository.getListTypeItemsMutableLD();

    }

    public LiveData<String> getTestLiveData() { return  itemListRepository.getTestLiveData(); }

    void setCategory(String category) {
        if (!currentCategory.equals(category)) {
            itemListRepository.fetchListTypeItems(category);
        }
        this.currentCategory = category;
    }
}
