package io.lundie.stockpile.features.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.AuthCredential;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.SingleLiveEvent;
import timber.log.Timber;

public class AuthViewModel extends FeaturesBaseViewModel {

    private static boolean attemptingRegistration;
    private UserRepository userRepository;
    private SingleLiveEvent<RequestSignInEvent> requestSignInEvent = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

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
        if(attemptingRegistration) {
            requestSignInEvent.setValue(new RequestSignInEvent(SignInStatusType.SUCCESS));
            attemptingRegistration = false;
        }
    }

    @Override
    public void onSignInFailed() {
        if(attemptingRegistration) {
            requestSignInEvent.setValue(new RequestSignInEvent(SignInStatusType.FAIL_AUTH));
            attemptingRegistration = false;
        }
    }

    @Override
    public void onRequestSignIn() {
        super.onRequestSignIn();
        isLoading.setValue(false);
    }

    void signInWithGoogle(AuthCredential credential, String displayName, String email) {
        attemptingRegistration = true;
        getUserManager().authAccountWithGoogle(credential, displayName, email);
    }

    void signInAnonymously() {
        attemptingRegistration = true;
        getUserManager().signInAnonymously();
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    void setIsLoading(boolean isLoading) {
        this.isLoading.setValue(isLoading);
    }

    SingleLiveEvent<RequestSignInEvent> getRequestSignInEvent() {
        return requestSignInEvent;
    }

    void clearUserRepository() {
        userRepository.clear();
    }
}
