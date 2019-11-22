package io.lundie.stockpile.features.stocklist.categorylist;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.repository.UserRepository;

public class CategoryViewModel extends ViewModel {

    private static final String LOG_TAG = CategoryViewModel.class.getSimpleName();

    private UserRepository userRepository;

    private MutableLiveData<ArrayList<ItemCategory>> itemCategoriesMLD = new MutableLiveData<ArrayList<ItemCategory>>();

    @Inject
    CategoryViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getTestLiveData() { return userRepository.getTestLiveData(); }

    public LiveData<ArrayList<ItemCategory>> getItemCategories() {

        if (itemCategoriesMLD.getValue() == null) {
            Log.i(LOG_TAG, "CatVM: Returning fresh data as cat data is null");
            itemCategoriesMLD = userRepository.getCategoryData();
            Log.i(LOG_TAG, "CatVM: Freshdata is: " + itemCategoriesMLD);
        } else {
            Log.i(LOG_TAG, "CatVM: Returning cached data");
        }
        return itemCategoriesMLD;
    }
}
