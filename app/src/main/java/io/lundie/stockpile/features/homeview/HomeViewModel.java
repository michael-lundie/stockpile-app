package io.lundie.stockpile.features;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class which is responsible for providing our data items to the UI.
 */
public class ItemsViewModel extends ViewModel {
    private LiveData<String> testLiveData;

    public LiveData<String> getTestLiveData() {
        return testLiveData;
    }
}
