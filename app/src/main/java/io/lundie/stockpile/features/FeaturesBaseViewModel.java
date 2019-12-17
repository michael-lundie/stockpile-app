package io.lundie.stockpile.features;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.SignInStatusObserver;
import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import io.lundie.stockpile.features.authentication.UserManager;
import timber.log.Timber;

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
public abstract class FeaturesBaseViewModel extends AndroidViewModel implements SignInStatusObserver {

    private static final String LOG_TAG = FeaturesBaseViewModel.class.getSimpleName();

    private UserManager userManager;
    private EventMessageController messageController;
    private String userID;
    private boolean isUserManagerInjected = false;
    private boolean isMessageControllerInjected = false;
    private boolean isObservingSignIn = false;

    public FeaturesBaseViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Method sets UserManager. As this is an extendable class,
     * @param userManager
     */
    @Inject
    public void setUserManager(UserManager userManager) {

        //User Manager should only be isUserManagerInjected. Boolean var prevents outside
        //access to this method, even though it is public. Using method injection, since this is
        //an extendable class and Dagger requires injectable methods to be public.
        if(!isUserManagerInjected) {
            this.userManager = userManager;
            observeSignInStatus();
            isUserManagerInjected = true;
        } else {
            Timber.e( "UserManager was already injected! Don't attempt to set manually.");
        }
    }

    /**
     * Method sets EventMessageController. As this is an extendable class,
     * @param eventMessageController
     */
    @Inject
    public void setEventMessageController(EventMessageController eventMessageController) {

        //User Manager should only be isUserManagerInjected. Boolean var prevents outside
        //access to this method, even though it is public. Using method injection, since this is
        //an extendable class and Dagger requires injectable methods to be public.
        if(!isMessageControllerInjected) {
            this.messageController = eventMessageController;
            observeSignInStatus();
            isMessageControllerInjected = true;
        } else {
            Timber.e("UserManager was already injected! Don't attempt to set manually.");
        }
    }

    public void onAttemptingSignIn() {}
    public void onSignInSuccess(String userID) {}
    public void onSignedInAnonymously(String userID) {}
    public void onSignInFailed() {}

    @Override
    public void update(@SignInStatusTypeDef int signInStatus) {
        Timber.e( "BaseVM: Update Called");
        switch(signInStatus) {
            case ATTEMPTING_SIGN_IN:
                onAttemptingSignIn();
                break;
            case SUCCESS:
                Timber.e("BaseVM: Success reported --> requesting userData.");
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
            Timber.e( "BaseVM: ADDING Observer");
            userManager.addObserver(this);
            isObservingSignIn = true;
        }
    }

    private void removeSignInStatusObserver() {
        if(isObservingSignIn) {
            Timber.e( "BaseVM: REMOVING Observer");
            userManager.removeObserver(this);
            isObservingSignIn = false;
        }
    }

    @Override
    protected void onCleared() {
        Timber.e( "BaseVM: On cleared called");
        // Ensures we won't leak our observer, must be called before super.
        removeSignInStatusObserver();
        super.onCleared();
    }

    public EventMessageController getMessageController() {
        return this.messageController;
    }
}
