package io.lundie.stockpile.features.stocklist.categorylist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;

public class CategoryViewModel extends FeaturesBaseViewModel {

    private static final String LOG_TAG = CategoryViewModel.class.getSimpleName();

    private UserRepository userRepository;

    private MediatorLiveData<ArrayList<ItemCategory>> itemCategoriesData = new MediatorLiveData<>();



    @Inject
    CategoryViewModel(@NonNull Application application, UserRepository userRepository) {
        super(application);
        this.userRepository = userRepository;
        addCategoryItemsLiveDataSource();
    }

    private void addCategoryItemsLiveDataSource() {
        if(userRepository.getUserDocumentRealTimeData() != null) {
            itemCategoriesData.addSource(userRepository.getUserDocumentRealTimeData(), snapshot -> {
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
