package io.lundie.stockpile.features.stocklist.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.data.repository.ItemRepository;

public class ItemViewModel extends ViewModel {

    private static final String LOG_TAG = ItemViewModel.class.getSimpleName();

    private ItemRepository itemRepository;

    @Inject
    ItemViewModel(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public LiveData<String> getTestData() {
        return itemRepository.getTestLiveData();
    }
}
