package io.lundie.stockpile.features;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.SignInStatusObserver;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;

import static io.lundie.stockpile.features.authentication.SignInStatusType.ATTEMPTING_SIGN_IN;
import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS_ANON;

/**
 * TODO: Add further Docs
 * IMPORTANT: Note that implemented Observer in this class is java.util.Observer. IT IS NOT
 * android lifecycle observer class.
 *
 */
public abstract class FeaturesBaseViewModel extends ViewModel implements SignInStatusObserver {

    private static final String LOG_TAG = FeaturesBaseViewModel.class.getSimpleName();

    private UserManager userManager;
    private String userID;
    private boolean isInjected = false;
    private boolean isObservingSignIn = false;

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

    public abstract void onAttemptingSignIn();
    public abstract void onSignInSuccess(String userID);
    public abstract void onSignedInAnonymously(String userID);
    public abstract void onSignInFailed();

    @Override
    public void update(@SignInStatusTypeDef int signInStatus) {
        Log.e(LOG_TAG, "BaseVM: Update Called");
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
    }

    public String getUserID() {
        return userID;
    }

    private void observeSignInStatus() {
        if(!isObservingSignIn) {
            Log.e(LOG_TAG, "BaseVM: ADDING Observer");
            userManager.addObserver(this);
            isObservingSignIn = true;
        }
    }

    private void removeSignInStatusObserver() {
        if(isObservingSignIn) {
            Log.e(LOG_TAG, "BaseVM: REMOVING Observer");
            userManager.removeObserver(this);
            isObservingSignIn = false;
        }
    }

    @Override
    protected void onCleared() {
        Log.e(LOG_TAG, "BaseVM: On cleared called");
        // Ensures we won't leak our observer, must be called before super.
        removeSignInStatusObserver();
        super.onCleared();
    }
}
