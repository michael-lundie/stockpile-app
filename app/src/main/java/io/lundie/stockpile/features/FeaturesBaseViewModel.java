package io.lundie.stockpile.features;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.SignInStatusObserver;
import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
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
public abstract class FeaturesBaseViewModel extends AndroidViewModel
        implements SignInStatusObserver {

    private UserManager userManager;
    private EventMessageController messageController;
    private String userID;
    private ItemPileBus itemPileBus;
    private boolean isUserManagerInjected = false;
    private boolean isMessageControllerInjected = false;
    private boolean isItemPileBusInjected = false;
    private boolean isObservingSignIn = false;

    public FeaturesBaseViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Method sets UserManager.
     * @param userManager
     */
    @Inject
    void setUserManager(UserManager userManager) {

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
     * Method sets EventMessageController.
     * @param eventMessageController
     */
    @Inject
    void setEventMessageController(EventMessageController eventMessageController) {

        if(!isMessageControllerInjected) {
            this.messageController = eventMessageController;
            isMessageControllerInjected = true;
        } else {
            Timber.e("EventMessageController was already injected! Don't attempt to set manually.");
        }
    }

    /**
     * Method sets EventMessageController.
     * @param itemPileBus
     */
    @Inject
    void setItemPileBus(ItemPileBus itemPileBus) {

        if(!isItemPileBusInjected) {
            this.itemPileBus = itemPileBus;
            isItemPileBusInjected = true;
            onItemPileBusInjected(itemPileBus);
        } else {
            Timber.e("ItemPileBus was already injected! Don't attempt to set manually.");
        }
    }


    public void onItemPileBusInjected(ItemPileBus itemPileBus) {}

    public ItemPileBus getItemPileBus() { return this.itemPileBus;}

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
