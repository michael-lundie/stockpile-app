package io.lundie.stockpile.features.stocklist.categorylist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.ItemCategory;
import io.lundie.stockpile.data.repository.CategoryRepository;

public class CategoryViewModel extends ViewModel {

    private CategoryRepository categoryRepository;

    @Inject
    CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public LiveData<String> getTestLiveData() { return categoryRepository.getTestLiveData(); }

    public LiveData<ArrayList<ItemCategory>> getItemTypes() { return categoryRepository.getItemTypes(); }
}
