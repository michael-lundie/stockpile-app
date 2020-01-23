package io.lundie.stockpile.features.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;

public class AuthViewModel extends FeaturesBaseViewModel {

    private static final String LOG_TAG = AuthViewModel.class.getSimpleName();
    private UserRepository userRepository;

    private LiveData<UserData> userLiveData;

    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> mailAddress = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();

    private MediatorLiveData<String> userNameErrorText = new MediatorLiveData<>();
    private MediatorLiveData<String> mailAddressErrorText = new MediatorLiveData<>();

    String userDisplayName = Transformations.map(
            userLiveData, userData -> userData.getDisplayName()).getValue();

    String userID;

    @Inject
    AuthViewModel(@NonNull Application application, UserRepository userRepository) {
        super(application);
        this.userRepository = userRepository;
    }

    @Override
    public void onAttemptingSignIn() {

    }

    @Override
    public void onSignInSuccess(String userID) {
        this.userID = userID;
//        userLiveData = userRepository.getUserLiveData(userID);
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

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(MutableLiveData<String> userName) {
        this.userName = userName;
    }

    public MutableLiveData<String> getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(MutableLiveData<String> mailAddress) {
        this.mailAddress = mailAddress;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MediatorLiveData<String> getUserNameErrorText() {
        return userNameErrorText;
    }

    public MediatorLiveData<String> getMailAddressErrorText() {
        return mailAddressErrorText;
    }
}
