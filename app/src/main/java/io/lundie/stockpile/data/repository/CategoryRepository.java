package io.lundie.stockpile.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.ItemCategory;

public class CategoryRepository {

    private MutableLiveData<String> testLiveData = new MutableLiveData<>();

    private MutableLiveData<ArrayList<ItemCategory>> itemTypes = new MutableLiveData<>();

    @Inject
    CategoryRepository() {}

    public LiveData<String> getTestLiveData() {
        if(testLiveData.getValue() == null) {
            testLiveData.postValue("TEST LiveDATA");
        } return testLiveData;
    }

    public LiveData<ArrayList<ItemCategory>> getItemTypes() {
        FakeCategoryData fakeCategoryData = new FakeCategoryData();
        if(itemTypes.getValue() == null) {
            itemTypes.postValue(fakeCategoryData.getItemTypesArrayList());
        } return itemTypes;
    }
}