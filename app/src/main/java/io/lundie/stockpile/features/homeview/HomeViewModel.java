package io.lundie.stockpile.features.homeview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.authentication.RequestSignInEvent;
import io.lundie.stockpile.utils.SingleLiveEvent;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.REQUEST_SIGN_IN;

/**
 * ViewModel class which is responsible for providing our data items to the UI.
 * Any pre-fetching from firestore should be done using the OnSignIn methods
 * provided by {@link FeaturesBaseViewModel}
 */
public class HomeViewModel extends FeaturesBaseViewModel {

    private UserRepository userRepository;
    private ItemListRepository itemListRepository;

    private boolean signedIn = false;

    private LiveData<String> testLiveData;
    private MediatorLiveData<String> userDisplayName = new MediatorLiveData<>();

    private LiveData<UserData> userLiveData;

    private MutableLiveData<ArrayList<ItemPile>> expiryList = new MutableLiveData<>();
    public SingleLiveEvent<PagingArrayStatusEvent> pagingStatusEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<RequestSignInEvent> requestSignInEvent = new SingleLiveEvent<>();

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
        signedIn = false;
        Timber.i("HomeViewModel reports: SIGN IN FAILED");
    }

    @Override
    public void onSignInSuccess(String userID) {
        super.onSignInSuccess(userID);
        Timber.i("HomeViewModel reports: SIGN IN SUCCESS");
        signedIn = true;
        getPagingExpiryList();
        //TODO: fetch user data and load everything into home view model
        userRepository.getUserDataSnapshot(getUserID());
        addUserDataLiveDataSource();
    }

    @Override
    public void onRequestSignIn() {
        requestSignInEvent.setValue(new RequestSignInEvent(REQUEST_SIGN_IN));
    }

    private void addUserDataLiveDataSource() {
        if(userRepository.getUserDocSnapshotLiveData() != null) {
            userDisplayName.addSource(userRepository.getUserDocSnapshotLiveData(), snapshot -> {
                if(snapshot != null) {
                    UserData data = snapshot.toObject(UserData.class);
                    if(data != null) {
                        userDisplayName.setValue(data.getDisplayName());
                    }
                }
            });
        }
    }

    public LiveData<String> getUserDisplayName() {
        return userRepository.getUserDisplayName();
    }

    public SingleLiveEvent<RequestSignInEvent> getRequestSignInEvent() {
        return requestSignInEvent;
    }

    public LiveData<ArrayList<ItemPile>> getPagingExpiryList() {
        if(signedIn && (expiryList == null || expiryList.getValue() == null)) {
            Timber.e("Paging --> Requesting repo");
            expiryList = itemListRepository.getPagingExpiryListLiveData(getUserID());
        }
        return expiryList;
    }

    public void loadNextExpiryListPage() {
        itemListRepository.getNextExpiryListPage(getUserID());
    }

    public SingleLiveEvent<PagingArrayStatusEvent> getPagingEvents() {
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
}
