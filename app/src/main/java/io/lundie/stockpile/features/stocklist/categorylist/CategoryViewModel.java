package io.lundie.stockpile.features.stocklist.categorylist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.data.model.firestore.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;

public class CategoryViewModel extends FeaturesBaseViewModel {

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

    LiveData<ArrayList<ItemCategory>> getItemCategoriesData() {
        return itemCategoriesData;
    }
}
