package io.lundie.stockpile.features;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.UserManager;

public abstract class FeaturesBaseViewModel extends ViewModel {

    private static final String LOG_TAG = FeaturesBaseViewModel.class.getSimpleName();

    private UserManager userManager;
    private String userID;

    @Inject
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public abstract void onAttemptingSignIn();
    public abstract void onSignInSuccess(String userID);
    public abstract void onSignedInAnnonomously(String userID);
    public abstract void onSignInFailed();

    private void observeSignInStatus() {
        userManager.getSignInStatus().observeForever(signInStatus -> {
            switch(signInStatus) {
                case ATTEMPTING_SIGN_IN:
                    onAttemptingSignIn();
                    break;
                case SUCCESS:
                    Log.e(LOG_TAG,"UserVM: Success reported --> requesting userData.");
                    userID = userManager.getUserID();
                    onSignInSuccess(userID);
                case SUCCESS_ANON:
                    onSignedInAnnonomously(userID);
                    break;
                case FAIL_AUTH:
                    onSignInFailed();
                    break;
            }
        });
    }
}
