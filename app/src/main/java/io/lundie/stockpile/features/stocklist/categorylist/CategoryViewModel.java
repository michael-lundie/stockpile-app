package io.lundie.stockpile.features.stocklist.categorylist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;

public class CategoryViewModel extends ViewModel {

    private static final String LOG_TAG = CategoryViewModel.class.getSimpleName();

    private UserRepository userRepository;

    private MediatorLiveData<ArrayList<ItemCategory>> itemCategoriesData = new MediatorLiveData<>();

    @Inject
    CategoryViewModel(UserRepository userRepository) {

        this.userRepository = userRepository;
        addCategoryItemsLiveDataSource();
    }

    public LiveData<String> getTestLiveData() { return userRepository.getTestLiveData(); }

    private void addCategoryItemsLiveDataSource() {
        if(userRepository.getUserDocSnapshotLiveData() != null) {
            itemCategoriesData.addSource(userRepository.getUserDocSnapshotLiveData(), snapshot -> {
                if(snapshot != null) {
                    UserData data = snapshot.toObject(UserData.class);
                    if(data != null) {
                        itemCategoriesData.setValue(data.getCategories());
                    }
                }
            });
        }
    }

    public LiveData<ArrayList<ItemCategory>> getItemCategoriesData() {

//        if (itemCategoriesData == null || itemCategoriesData.getValue() == null ) {
//            Log.i(LOG_TAG, "CatVM: Returning fresh data as cat data is null");
//
//            Log.i(LOG_TAG, "CatVM: Freshdata is: " + itemCategoriesData);
//        } else {
//            Log.i(LOG_TAG, "CatVM: Returning cached data");
//        }
        return itemCategoriesData;
    }
}
