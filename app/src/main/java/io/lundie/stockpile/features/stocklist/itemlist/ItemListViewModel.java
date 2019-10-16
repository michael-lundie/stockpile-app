package io.lundie.stockpile.features.stocklist.itemlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.data.repository.ItemListRepository;

public class ItemListViewModel extends ViewModel {

    private ItemListRepository itemListRepository;

    @Inject
    ItemListViewModel(ItemListRepository itemListRepository) {
        this.itemListRepository = itemListRepository;
    }

    public LiveData<String> getTestLiveData() { return  itemListRepository.getTestLiveData(); }

}
