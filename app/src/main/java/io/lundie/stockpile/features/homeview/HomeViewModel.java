package io.lundie.stockpile.features.homeview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import timber.log.Timber;

/**
 * ViewModel class which is responsible for providing our data items to the UI.
 * Any pre-fetching from firestore should be done using the OnSignIn methods
 * provided by {@link FeaturesBaseViewModel}
 */
public class HomeViewModel extends FeaturesBaseViewModel {

    private UserRepository userRepository;
    private ItemListRepository itemListRepository;

    private LiveData<String> testLiveData;
    private LiveData<String> userDisplayName;

    @Inject
    HomeViewModel(@NonNull Application application, UserRepository userRepository,
                  ItemListRepository itemListRepository) {
        super(application);
        this.userRepository = userRepository;
        this.itemListRepository = itemListRepository;
    }

    @Override
    public void onSignedInAnonymously(String userID) {
        Timber.i("HomeViewModel reports: SIGN IN ANNON");
        super.onSignedInAnonymously(userID);
    }

    @Override
    public void onSignInFailed() {
        super.onSignInFailed();
        Timber.i("HomeViewModel reports: SIGN IN FAILED");
    }

    @Override
    public void onSignInSuccess(String userID) {

        super.onSignInSuccess(userID);
        Timber.i("HomeViewModel reports: SIGN IN SUCCESS");
        itemListRepository.fetchExpiringItems(getUserID(), 0);
    }

    public LiveData<String> getUserDisplayName() {
        return userRepository.getUserDisplayName();
    }

    public LiveData<String> getTestLiveData() {
        return userRepository.getHomeLiveData();
    }

    public LiveData<ArrayList<ItemPile>> getExpiringItemsList() {
        return itemListRepository.getExpiryListLiveData();
    }
}
