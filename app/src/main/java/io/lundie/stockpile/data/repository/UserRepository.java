package io.lundie.stockpile.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class UserRepository {

    private MutableLiveData<String> homeLiveData = new MutableLiveData<>();
    private MutableLiveData<String> userDisplayName = new MutableLiveData<>();

    @Inject
    UserRepository() { }

    public LiveData<String> getHomeLiveData() {
        if(homeLiveData.getValue() == null) {
            homeLiveData.postValue("Test String");
        }

        return homeLiveData;
    }

    public MutableLiveData<String> getUserDisplayName() {
        if (userDisplayName.getValue() == null) {
            userDisplayName.postValue("Michael");
        }
        return userDisplayName;
    }
}