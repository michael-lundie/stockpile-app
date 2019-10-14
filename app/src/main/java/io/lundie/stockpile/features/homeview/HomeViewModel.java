package io.lundie.stockpile.features.homeview;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

/**
 * ViewModel class which is responsible for providing our data items to the UI.
 */
public class HomeViewModel extends ViewModel {

    private HomeRepository homeRepository;

    private LiveData<String> testLiveData;
    private LiveData<String> userDisplayName;

    @Inject
    HomeViewModel(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    public LiveData<String> getUserDisplayName() {
        return homeRepository.getUserDisplayName();
    }

    public LiveData<String> getTestLiveData() {
        return homeRepository.getHomeLiveData();
    }
}
