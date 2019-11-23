package io.lundie.stockpile.features;

import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.utils.SignInStatus;

public abstract class FeaturesBaseViewModel extends ViewModel {

    private static final String LOG_TAG = FeaturesBaseViewModel.class.getSimpleName();

    private UserManager userManager;
    private String userID;
    private boolean isInjected = false;
    private boolean isObservingSignIn = false;

    private Observer<SignInStatus> observer;

    /**
     * Method sets UserManager. As this is an extendable class,
     * @param userManager
     */
    @Inject
    public void setUserManager(UserManager userManager) {

        //User Manager should only be isInjected. Boolean var prevents outside
        //access to this method, even though it is public. Using method injection, since this is
        //an extendable class and Dagger requires injectable methods to be public.
        if(!isInjected) {
            this.userManager = userManager;
            observeSignInStatus();
            isInjected = true;
        } else {
            Log.e(LOG_TAG, "UserManager was already injected! Don't attempt to set manually.");
        }
    }

    public void startSignInStatusObserver() { observeSignInStatus(); }
    public void stopSignInStatusObserver() { removeSignInStatusObserver(); }

    public abstract void onAttemptingSignIn();
    public abstract void onSignInSuccess(String userID);
    public abstract void onSignedInAnonymously(String userID);
    public abstract void onSignInFailed();

    private void observeSignInStatus() {
        if(!isObservingSignIn) {
            observer = signInStatus -> {
                switch(signInStatus) {
                    case ATTEMPTING_SIGN_IN:
                        onAttemptingSignIn();
                        break;
                    case SUCCESS:
                        Log.e(LOG_TAG,"BaseVM: Success reported --> requesting userData.");
                        userID = userManager.getUserID();
                        onSignInSuccess(userID);
                    case SUCCESS_ANON:
                        //TODO: Sort out the anonymous signing in method.
                        onSignedInAnonymously(userID);
                        break;
                    case FAIL_AUTH:
                        onSignInFailed();
                        break;
                }
            };
            userManager.getSignInStatus().observeForever(observer);
            isObservingSignIn = true;
        }
    }

    private void removeSignInStatusObserver() {
        if(isObservingSignIn) {
            userManager.getSignInStatus().removeObserver(observer);
            isObservingSignIn = false;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Ensures we won't leak our observer
        removeSignInStatusObserver();
        isObservingSignIn = false;
    }
}
