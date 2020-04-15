package io.lundie.stockpile.features.homeview;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.model.firestore.Target;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.data.repository.ItemListRepositoryUtils.PagingArrayStatusEvent;
import io.lundie.stockpile.data.repository.ItemListRepositoryUtils.PagingArrayStatusType;
import io.lundie.stockpile.data.repository.TargetsRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.TransactionStatusController;
import io.lundie.stockpile.features.TransactionUpdateIdType;
import io.lundie.stockpile.features.authentication.RequestSignInEvent;
import io.lundie.stockpile.features.authentication.SignInStatusType;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.features.widget.ExpiringItemsWidgetProvider;
import io.lundie.stockpile.utils.SingleLiveEvent;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.REQUEST_SIGN_IN;
import static io.lundie.stockpile.features.widget.ExpiringItemsWidgetProvider.DISABLE_FOR_SIGNOUT;

/**
 * ViewModel class which is responsible for providing data and managing state of the UI.
 * Any pre-fetching from firestore should be done using the OnSignIn methods
 * provided by {@link FeaturesBaseViewModel}
 */
public class HomeViewModel extends FeaturesBaseViewModel {

    private UserRepository userRepository;
    private ItemListRepository itemListRepository;
    private TargetsRepository targetsRepository;
    private AppExecutors appExecutors;

    private MutableLiveData<ArrayList<ItemPile>> expiryList = new MutableLiveData<>();
    private SingleLiveEvent<PagingArrayStatusEvent> pagingStatusEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<RequestSignInEvent> signInStatusLiveEvents = new SingleLiveEvent<>();
    private MediatorLiveData<ArrayList<Target>> targetsLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> userDisplayName = new MediatorLiveData<>();
    private MutableLiveData<Boolean> isExpiringItemsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isExpiringItemsRefreshing = new MutableLiveData<>(false);

    private boolean signedIn = false;
    private boolean attemptingRegistration = false;


    @Inject
    HomeViewModel(@NonNull Application application, UserRepository userRepository,
                  ItemListRepository itemListRepository, TargetsRepository targetsRepository,
                  AppExecutors appExecutors) {
        super(application);
        this.userRepository = userRepository;
        this.itemListRepository = itemListRepository;
        this.targetsRepository = targetsRepository;
        this.appExecutors = appExecutors;
    }

    @Override
    public void onSignedInAnonymously(String userID) {
        Timber.i("HomeViewModel reports: SIGN IN ANNON");
        super.onSignedInAnonymously(userID);
    }

    @Override
    public void onSignInFailed() {
        super.onSignInFailed();
        if(attemptingRegistration) {
            signInStatusLiveEvents.setValue(new RequestSignInEvent(SignInStatusType.FAIL_AUTH));
            attemptingRegistration = false;
        }
        signedIn = false;
        Timber.i("HomeViewModel reports: SIGN IN IMAGE_FAILED");
    }

    @Override
    public void onSignInSuccess(String userID) {
        if(attemptingRegistration) {
            signInStatusLiveEvents.setValue(new RequestSignInEvent(SignInStatusType.SUCCESS));
            attemptingRegistration = false;
        }
        Timber.i("UserData: HomeViewModel reports: SIGN IN SUCCESS. UserID: %s", userID);
        signedIn = true;
        getPagingExpiryList();
        //TODO: fetch user data and load everything into home view model
        userRepository.initUserDocumentRealTimeUpdates(userID);
    }

    private void initMediatorData() {
//        userDisplayName = Transformations.map(userRepository.getUserMediatorData(), UserData::getDisplayName);
        //TODO: we might not need to call initMediatorData from onSignInSuccess once we use the new
        // log-in model
        if(userDisplayName.getValue() == null) {
            userDisplayName.addSource(userRepository.getUserMediatorData(), userData -> {
                if(userData != null) {
                    userDisplayName.setValue(userData.getDisplayName());
                }
            });
        }
    }

    @Override
    protected void onCleared() {
        userDisplayName.removeSource(userRepository.getUserMediatorData());
        super.onCleared();
    }

    @Override
    public void onRequestSignIn() {
        signInStatusLiveEvents.setValue(new RequestSignInEvent(REQUEST_SIGN_IN));
    }

    @Override
    public void onSignOut() {
        Timber.e("UserData: onSignOut");
        userDisplayName.removeSource(userRepository.getUserMediatorData());
        resetHomeViewModel();
        userRepository.clear();
        itemListRepository.clear();
    }

    void linkAndRegisterAnonymousAccount(AuthCredential credential, String displayName, String email) {
        UserManager userManager = getUserManager();
        if(userManager != null) {
            attemptingRegistration = true;
            userManager.registerWithAnonymousAccount(credential, displayName, email,
                    userRepository.fetchMostRecentUserDocumentData(getUserID()));
        }
    }

    public LiveData<String> getUserDisplayName() {
        if(userDisplayName == null || userDisplayName.getValue() == null) {
            initMediatorData();
        } return userDisplayName;
    }

    SingleLiveEvent<RequestSignInEvent> getSignInStatusLiveEvents() {
        return signInStatusLiveEvents;
    }

    LiveData<ArrayList<Target>> getTargetsLiveData() {
        if(isUserSignedIn() && targetsLiveData.getValue() == null) {
            targetsLiveData.addSource(targetsRepository.getTargets(getUserID()), documentSnapshots -> {

                if(documentSnapshots != null) {
                    final TransactionStatusController.EventPacket eventPacket =
                            getStatusController().getEventPacket(TransactionUpdateIdType.TARGET_UPDATE_ID);

                    appExecutors.diskIO().execute(() -> {
                        ArrayList<Target> targets = new ArrayList<>();
                        for(DocumentSnapshot document : documentSnapshots) {
                            Target target = document.toObject(Target.class);
                            targets.add(target);
                            if(eventPacket != null &&
                                    target.getTargetName().equals(eventPacket.getStringFieldID())) {
                                eventPacket.setEventMessage(getApplication().getResources().getString(R.string.event_msg_target_added));
                                postTransactionEvent(eventPacket);
                            }
                        }
                        targetsLiveData.postValue(targets);
                    });
                } else {
                    targetsLiveData.setValue(null);
                }
            });
        }
        return targetsLiveData;
    }

    void setTargetListBus() {
        if(getTargetsListBus() != null) {
            getTargetsListBus().setTargets(targetsLiveData.getValue());
        }
    }

    LiveData<ArrayList<ItemPile>> getPagingExpiryList() {
        if(signedIn && (expiryList == null || expiryList.getValue() == null)) {
            setIsExpiringItemsLoading(true);
            expiryList = itemListRepository.getPagingExpiryListLiveData(getUserID());
        }
        return expiryList;
    }

    void loadNextExpiryListPage() {
        itemListRepository.fetchNextExpiryListPage(getUserID());
    }

    public void onExpiryListRefresh() {
        expiryList.setValue(new ArrayList<>());
        expiryList = itemListRepository.getPagingExpiryListLiveData(getUserID());
        isExpiringItemsRefreshing.setValue(true);
    }

    public LiveData<Boolean> getIsExpiringItemsRefreshing() {
        return isExpiringItemsRefreshing;
    }

    SingleLiveEvent<PagingArrayStatusEvent> getPagingEvents() {
        itemListRepository.setPagingStatusListener(new ItemListRepository.PagingStatusListener() {
            @Override
            public void onStop() {
                setExpiringItemsLoadingFalse();
                pagingStatusEvent.setValue(new PagingArrayStatusEvent(PagingArrayStatusType.LOAD_STOP));
            }

            @Override
            public void onError() {
                setExpiringItemsLoadingFalse();
                pagingStatusEvent.setValue(new PagingArrayStatusEvent(PagingArrayStatusType.LOAD_FAIL));
            }

            @Override
            public void onLoaded() {
                setExpiringItemsLoadingFalse();
                pagingStatusEvent.setValue(new PagingArrayStatusEvent(PagingArrayStatusType.LOAD_SUCCESS));
            }
        });
        return pagingStatusEvent;
    }

    private void setExpiringItemsLoadingFalse() {
        setIsExpiringItemsLoading(false);
        isExpiringItemsRefreshing.setValue(false);
    }

    public LiveData<Boolean> getIsExpiringItemsLoading() {
        return isExpiringItemsLoading;
    }

    void setIsExpiringItemsLoading(Boolean isLoading) {
        isExpiringItemsLoading.setValue(isLoading);
    }

    void broadcastToWidget(boolean disableForSignOut) {

        AppWidgetManager man = AppWidgetManager.getInstance(getApplication());
        int[] ids = man.getAppWidgetIds(
                new ComponentName(getApplication(),ExpiringItemsWidgetProvider.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(ExpiringItemsWidgetProvider.APP_WIDGET_EXPIRING_ID, ids);
        updateIntent.putExtra(DISABLE_FOR_SIGNOUT, disableForSignOut);
        getApplication().sendBroadcast(updateIntent);
    }

    @Override
    public void signOutUser() {
        disableWidgets();
        super.signOutUser();
    }

    private void resetHomeViewModel() {
        expiryList.setValue(new ArrayList<>());
        targetsLiveData.setValue(new ArrayList<>());
    }

    private void disableWidgets() {
        broadcastToWidget(true);
    }
}