package io.lundie.stockpile.features;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.SignInStatusObserver;
import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.features.homeview.TargetListBus;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import io.lundie.stockpile.features.targets.TargetBus;
import io.lundie.stockpile.utils.SingleLiveEvent;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.ATTEMPTING_SIGN_IN;
import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.REQUEST_SIGN_IN;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SIGN_OUT;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS_ANON;

/**
 * This class provides each extending ViewModel access to {@link UserManager} public methods and
 * listeners. Data which is to be "pre-fetched" on instantiation of a ViewModel should be
 * called from the appropriate method - generally {@link #onSignInSuccess(String)}.
 * Edge cases may be handled using {@link #onSignInFailed()} and {@link #onAttemptingSignIn()}.
 * <p>
 * This class also provides access to an {@link ItemPileBus}. As {@link FeaturesBaseViewModel} is
 * {@link io.lundie.stockpile.injection.AppScope}, all view models extending from this class have
 * access to it's objects. {@link TransactionStatusController} works similarly.
 * <p>
 * Fragments and Activities are still responsible for saving state (at a minimum level) on the event
 * the application is destroyed. It is also important that any pre-fetches carried out are not
 * repeated unnecessarily. Check scopes of each ViewModel instantiation and handle as appropriate.
 */
public abstract class FeaturesBaseViewModel extends AndroidViewModel
        implements SignInStatusObserver {

    private static String userID;
    private static SingleLiveEvent<TransactionStatusController.EventPacket> transactionEvent;
    private UserManager userManager;
    private TransactionStatusController statusController;
    private ItemPileBus itemPileBus;
    private TargetListBus targetsListBus;
    private TargetBus targetBus;
    private boolean isUserManagerInjected = false;
    private boolean isTransactionStatusControllerInjected = false;
    private boolean isTargetsBusInjected = false;
    private boolean isItemPileBusInjected = false;
    private boolean isTargetBusInjected = false;
    private boolean isObservingSignIn = false;
    private boolean isUserSignedIn = false;

    public FeaturesBaseViewModel(@NonNull Application application) {
        super(application);
        transactionEvent = new SingleLiveEvent<>();
    }

    private void onUserManagerInjected() {
        //Fetches the User
        userManager.init();
    }

    /**
     * Method sets TransactionStatusController.
     *
     * @param transactionStatusController
     */
    @Inject
    void setTransactionStatusController(TransactionStatusController transactionStatusController) {

        if (!isTransactionStatusControllerInjected) {
            this.statusController = transactionStatusController;
            isTransactionStatusControllerInjected = true;
        } else {
            Timber.e("TransactionStatusController was already injected! Don't attempt to set manually.");
        }
    }

    public void onTargetsListBusInjected(TargetListBus targetsListBus) { }

    public TargetListBus getTargetsListBus() {
        return this.targetsListBus;
    }

    /**
     * Method sets Targets Bus.
     *
     * @param targetsListBus
     */
    @Inject
    void setTargetsListBus(TargetListBus targetsListBus) {

        if (!isTargetsBusInjected) {
            this.targetsListBus = targetsListBus;
            isTargetsBusInjected = true;
            onTargetsListBusInjected(targetsListBus);
        } else {
            Timber.e("TargetsListBus was already injected! Don't attempt to set manually.");
        }
    }

    public void onTargetBusInjected(TargetBus targetBus) { }

    public TargetBus getTargetBus() {
        return this.targetBus;
    }

    /**
     * Method sets Targets Bus.
     *
     * @param targetBus
     */
    @Inject
    void setTargetBus(TargetBus targetBus) {

        if (!isTargetBusInjected) {
            this.targetBus = targetBus;
            isTargetBusInjected = true;
            onTargetBusInjected(targetBus);
        } else {
            Timber.e("TargetsListBus was already injected! Don't attempt to set manually.");
        }
    }

    public void onItemPileBusInjected(ItemPileBus itemPileBus) { }

    protected ItemPileBus getItemPileBus() {
        return this.itemPileBus;
    }

    /**
     * Method sets TransactionStatusController.
     *
     * @param itemPileBus
     */
    @Inject
    void setItemPileBus(ItemPileBus itemPileBus) {

        if (!isItemPileBusInjected) {
            this.itemPileBus = itemPileBus;
            isItemPileBusInjected = true;
            onItemPileBusInjected(itemPileBus);
        } else {
            Timber.e("ItemPileBus was already injected! Don't attempt to set manually.");
        }
    }

    public void onAttemptingSignIn() { }

    public void onSignInSuccess(String userID) {
    }

    public void onSignedInAnonymously(String userID) {
    }

    public void onRequestSignIn() { }

    public void onSignInFailed() { }

    public void onSignOut() {}

    @Override
    public void update(@SignInStatusTypeDef int signInStatus) {
        switch (signInStatus) {
            case ATTEMPTING_SIGN_IN:
                onAttemptingSignIn();
                break;
            case SUCCESS_ANON:
                userID = userManager.getUserID();
                onSignedInAnonymously(userID);
            case SUCCESS:
                userID = userManager.getUserID();
                isUserSignedIn = true;
                onSignInSuccess(userID);
                break;
            case FAIL_AUTH:
                isUserSignedIn = false;
                onSignInFailed();
                break;
            case REQUEST_SIGN_IN:
                onRequestSignIn();
                break;
            case SIGN_OUT:
                onSignOut();
                break;
        }
    }

    public String getUserID() {
        return userID;
    }

    public boolean isUserSignedIn() {
        return isUserSignedIn;
    }

    private void observeSignInStatus() {
        if (!isObservingSignIn) {
            userManager.addObserver(this);
            isObservingSignIn = true;
        }
    }

    private void removeSignInStatusObserver() {
        if (isObservingSignIn) {
            userManager.removeObserver(this);
            isObservingSignIn = false;
        }
    }

    @Override
    protected void onCleared() {
        // Ensures we won't leak our observer, must be called before super.
        removeSignInStatusObserver();
        super.onCleared();
    }

    public void signOutUser() {
        getUserManager().signOutUser();
    }

    protected UserManager getUserManager() {
        return userManager;
    }

    /**
     * Method sets UserManager.
     *
     * @param userManager
     */
    @Inject
    void setUserManager(UserManager userManager) {
        //User Manager should only be isUserManagerInjected. Boolean var prevents outside
        //access to this method, even though it is public. Using method injection, since this is
        //an extendable class and Dagger requires injectable methods to be public.
        if (!isUserManagerInjected) {
            this.userManager = userManager;
            onUserManagerInjected();
            observeSignInStatus();
            isUserManagerInjected = true;
        } else {
            Timber.e("UserManager was already injected! Don't attempt to set manually.");
        }
    }

    public TransactionStatusController getStatusController() {
        return this.statusController;
    }

    LiveData<TransactionStatusController.EventPacket> getTransactionEvent() {
        return transactionEvent;
    }

    public void setTransactionEvent(TransactionStatusController.EventPacket eventPacket) {
        transactionEvent.setValue(eventPacket);
    }

    protected void postTransactionEvent(TransactionStatusController.EventPacket eventPacket) {
        transactionEvent.postValue(eventPacket);
    }
}
