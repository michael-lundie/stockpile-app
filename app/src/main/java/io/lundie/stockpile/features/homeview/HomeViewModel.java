package io.lundie.stockpile.features.homeview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.AuthCredential;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.model.Target;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.authentication.RequestSignInEvent;
import io.lundie.stockpile.features.authentication.SignInStatusType;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.utils.SingleLiveEvent;
import timber.log.Timber;

import static io.lundie.stockpile.data.repository.UserRepositoryUtils.UserLiveDataStatusType.*;
import static io.lundie.stockpile.features.authentication.SignInStatusType.REQUEST_SIGN_IN;

/**
 * ViewModel class which is responsible for providing our data items to the UI.
 * Any pre-fetching from firestore should be done using the OnSignIn methods
 * provided by {@link FeaturesBaseViewModel}
 */
public class HomeViewModel extends FeaturesBaseViewModel implements Observer {

    private UserRepository userRepository;
    private ItemListRepository itemListRepository;

    private MutableLiveData<ArrayList<ItemPile>> expiryList = new MutableLiveData<>();
    private SingleLiveEvent<PagingArrayStatusEvent> pagingStatusEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<RequestSignInEvent> requestSignInEvent = new SingleLiveEvent<>();
    private MediatorLiveData<ArrayList<Target>> targets = new MediatorLiveData<>();
    private MediatorLiveData<String> userName = new MediatorLiveData<>();

    private boolean signedIn = false;
    private boolean attemptingRegistration = false;

    @Inject
    HomeViewModel(@NonNull Application application, UserRepository userRepository,
                  ItemListRepository itemListRepository) {
        super(application);
        this.userRepository = userRepository;
        this.itemListRepository = itemListRepository;
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
            requestSignInEvent.setValue(new RequestSignInEvent(SignInStatusType.FAIL_AUTH));
            attemptingRegistration = false;
        }
        signedIn = false;
        Timber.i("HomeViewModel reports: SIGN IN FAILED");
    }

    @Override
    public void onSignInSuccess(String userID) {
        super.onSignInSuccess(userID);
        if(attemptingRegistration) {
            requestSignInEvent.setValue(new RequestSignInEvent(SignInStatusType.SUCCESS));
            attemptingRegistration = false;
        }
        Timber.i("HomeViewModel reports: SIGN IN SUCCESS");
        signedIn = true;
        getPagingExpiryList();
        //TODO: fetch user data and load everything into home view model
        userRepository.initUserDocumentRealTimeUpdates(getUserID());
        initMediatorData();
    }

    private void initMediatorData() {
        userName.addSource(userRepository.getUserMediatorData(), userData -> {
            userName.setValue(userData.getDisplayName());
        });
    }

    @Override
    public void onRequestSignIn() {
        requestSignInEvent.setValue(new RequestSignInEvent(REQUEST_SIGN_IN));
    }

    void linkAndRegisterAnonymousAccount(AuthCredential credential, String displayName, String email) {
        UserManager userManager = getUserManager();
        if(userManager != null) {
            attemptingRegistration = true;
            userManager.registerWithAnonymousAccount(credential, displayName, email,
                    userRepository.fetchMostRecentUserDocumentData(getUserID()));
        }
    }

    public LiveData<String> getUserDisplayName() { return userName; }

    SingleLiveEvent<RequestSignInEvent> getRequestSignInEvent() {
        return requestSignInEvent;
    }

    LiveData<ArrayList<Target>> getTargetsLiveData() {
        if(targets.getValue() == null) {
            targets.addSource(userRepository.getUserMediatorData(), userData -> {
                targets.setValue(userData.getTargets());
            });
        }
        return targets;
    }

    LiveData<ArrayList<ItemPile>> getPagingExpiryList() {
        if(signedIn && (expiryList == null || expiryList.getValue() == null)) {
            Timber.e("Paging --> Requesting repo");
            expiryList = itemListRepository.getPagingExpiryListLiveData(getUserID());
        }
        return expiryList;
    }

    void loadNextExpiryListPage() {
        itemListRepository.getNextExpiryListPage(getUserID());
    }

    SingleLiveEvent<PagingArrayStatusEvent> getPagingEvents() {
        itemListRepository.setPagingStatusListener(new ItemListRepository.PagingStatusListener() {
            @Override
            public void onStop() {
                pagingStatusEvent.setValue(new PagingArrayStatusEvent(PagingArrayStatusType.LOAD_STOP));
            }

            @Override
            public void onError() {
                pagingStatusEvent.setValue(new PagingArrayStatusEvent(PagingArrayStatusType.LOAD_FAIL));
            }

            @Override
            public void onLoaded() {
                pagingStatusEvent.setValue(new PagingArrayStatusEvent(PagingArrayStatusType.LOAD_SUCCESS));
            }
        });
        return pagingStatusEvent;
    }

    @Override
    public void update(Observable observable, Object object) {
        if(object != null) {
            switch((int) object) {
                case DATA_AVAILABLE:
                    Timber.e("REPO: Data is available.");
                    break;
                case FETCHING:
                    Timber.e("REPO: Data is fetching.");
                    break;
                case FAILED:
                    Timber.e("REPO: Data FAILED");
                    break;
            }
        }
    }
}
