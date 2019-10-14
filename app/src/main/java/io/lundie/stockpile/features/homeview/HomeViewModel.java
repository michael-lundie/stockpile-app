package io.lundie.stockpile.features.homeview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.data.repository.UserRepository;

/**
 * ViewModel class which is responsible for providing our data items to the UI.
 */
public class HomeViewModel extends ViewModel {

    private UserRepository userRepository;

    private LiveData<String> testLiveData;
    private LiveData<String> userDisplayName;

    @Inject
    HomeViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<String> getUserDisplayName() {
        return userRepository.getUserDisplayName();
    }

    public LiveData<String> getTestLiveData() {
        return userRepository.getHomeLiveData();
    }
}
