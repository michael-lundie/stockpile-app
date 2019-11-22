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
import io.lundie.stockpile.utils.SignInStatus;

public class UserViewModel extends ViewModel {

    private static final String LOG_TAG = UserViewModel.class.getSimpleName();
    private UserRepository userRepository;
    private UserManager userManager;

    private final MediatorLiveData<SignInStatus> signInStatus = new MediatorLiveData<>();

    private LiveData<UserData> userLiveData;

    String userDisplayName = Transformations.map(
            userLiveData, userData -> userData.getDisplayName()).getValue();

    String userID;

    @Inject
    UserViewModel(UserRepository userRepository, UserManager userManager) {
        this.userRepository = userRepository;
        this.userManager = userManager;

        // Requests user data from the repository when sign-in is complete,
        // to ensure we have
        userManager.getSignInStatus().observeForever(signInStatus -> {
            switch(signInStatus) {
                case SUCCESS:
                    Log.e(LOG_TAG,"UserVM: Success reported --> requesting userData.");
                    userID = userManager.getUserID();
                    userLiveData = userRepository.getUserLiveData(userID);
            }
        });
    }

    public LiveData<SignInStatus> getSignInStatus() {
        return userManager.getSignInStatus();
    }

    public String getUserID() {
        return userID;
    }
}
