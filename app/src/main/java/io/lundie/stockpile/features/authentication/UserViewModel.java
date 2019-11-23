package io.lundie.stockpile.features.authentication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.SignInStatus;

public class UserViewModel extends FeaturesBaseViewModel {

    private static final String LOG_TAG = UserViewModel.class.getSimpleName();
    private UserRepository userRepository;

    private LiveData<UserData> userLiveData;

    String userDisplayName = Transformations.map(
            userLiveData, userData -> userData.getDisplayName()).getValue();

    String userID;

    @Inject
    UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAttemptingSignIn() {

    }

    @Override
    public void onSignInSuccess(String userID) {
        this.userID = userID;
        userLiveData = userRepository.getUserLiveData(userID);
    }

    @Override
    public void onSignedInAnonymously(String userID) {

    }

    @Override
    public void onSignInFailed() {

    }

    public String getUserID() {
        return userID;
    }
}
