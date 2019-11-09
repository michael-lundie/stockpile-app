package io.lundie.stockpile.features.authentication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.utils.SignInStatus;

import static io.lundie.stockpile.utils.SignInStatus.*;

public class UserViewModel extends ViewModel {

    private static final String LOG_TAG = UserViewModel.class.getSimpleName();

    private MutableLiveData<SignInStatus> signInStatus = new MutableLiveData<>();

    private UserRepository userRepository;
    private FirebaseAuth firebaseAuth;

    private FirebaseUser currentUser;

    @Inject
    UserViewModel(UserRepository userRepository, FirebaseAuth firebaseAuth) {
        this.userRepository = userRepository;
        this.firebaseAuth = firebaseAuth;
        fetchUser();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public String getUserUID() { return  currentUser.getUid(); }

    //TODO: Might be better to have a data observer. On
    // success, get activity to fetch the user name.
    private FirebaseUser fetchUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Sign In: User signed in.");
            currentUser = user;
            setSignInStatus(SUCCESS);
            return user;
        } else {
            Log.d(LOG_TAG, "Sign In: Attempting sign-in anon.");
            setSignInStatus(ATTEMPTING_SIGN_IN);
            signInAnonymously();
        }
        return currentUser;
    }

    private void signInAnonymously() {

        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        setSignInStatus(SUCCESS_ANON);
                    } else {
                        currentUser = null;
                        setSignInStatus(FAIL_AUTH);
                    }
                });
    }

    public LiveData<SignInStatus> getSignInStatus() {
        return signInStatus;
    }

    private void setSignInStatus(SignInStatus status) {
        this.signInStatus.postValue(status);
    }
}
