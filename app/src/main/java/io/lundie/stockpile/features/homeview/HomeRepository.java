package io.lundie.stockpile.features.homeview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class HomeRepository {

    private MutableLiveData<String> homeLiveData = new MutableLiveData<>();
    private MutableLiveData<String> userDisplayName = new MutableLiveData<>();

    @Inject
    public HomeRepository() {
    }

    LiveData<String> getHomeLiveData() {
        if(homeLiveData.getValue() == null) {
            homeLiveData.postValue("Test String");
        }

        return homeLiveData;
    }

    MutableLiveData<String> getUserDisplayName() {
        if (userDisplayName.getValue() == null) {
            userDisplayName.postValue("Michael");
        }
        return userDisplayName;
    }
}
