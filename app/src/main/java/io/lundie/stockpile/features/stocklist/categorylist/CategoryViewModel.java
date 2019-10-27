package io.lundie.stockpile.features.stocklist.categorylist;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.ItemCategory;
import io.lundie.stockpile.data.repository.UserRepository;

public class CategoryViewModel extends ViewModel {

    private static final String LOG_TAG = CategoryViewModel.class.getSimpleName();

    private UserRepository userRepository;

    private MutableLiveData<ArrayList<ItemCategory>> itemCategoriesMLD = new MutableLiveData<>();

    @Inject
    CategoryViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getTestLiveData() { return userRepository.getTestLiveData(); }

    public LiveData<ArrayList<ItemCategory>> getItemTypes() {

        if (itemCategoriesMLD.getValue() == null) {
            Log.i(LOG_TAG, "Returning fresh data as cat data is null");
            itemCategoriesMLD = userRepository.getCategoryData();
        } else {
            Log.i(LOG_TAG, "Returning cached data");
        }
        return itemCategoriesMLD;
    }
}
