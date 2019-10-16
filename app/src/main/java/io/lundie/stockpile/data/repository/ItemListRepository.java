package io.lundie.stockpile.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class ItemListRepository {

    private MutableLiveData<String> testLiveData = new MutableLiveData<>();

    @Inject
    ItemListRepository() {}

    public LiveData<String> getTestLiveData() {
        if(testLiveData.getValue() == null) {
            testLiveData.postValue("TEST LiveDATA");
        } return testLiveData;
    }

}
