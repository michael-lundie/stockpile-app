package io.lundie.stockpile.features.authentication;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import io.lundie.stockpile.utils.SignInStatus;

import static io.lundie.stockpile.utils.SignInStatus.ATTEMPTING_SIGN_IN;
import static io.lundie.stockpile.utils.SignInStatus.FAIL_AUTH;
import static io.lundie.stockpile.utils.SignInStatus.SUCCESS;
import static io.lundie.stockpile.utils.SignInStatus.SUCCESS_ANON;

public class UserManager {

    private static final String LOG_TAG = UserManager.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userID;

    private MutableLiveData<SignInStatus> signInStatus = new MutableLiveData<>();

    @Inject
    public UserManager(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        fetchUser();
    }

    public String getUserID() {
        if(!userID.isEmpty()) {
            return this.userID;
        } return null;
    }

    public FirebaseUser getCurrentUser() {
        if(currentUser != null) {
            return this.currentUser;
        } return null;
    }

    //TODO: Might be better to have a data observer. On
    // success, get activity to fetch the user name.
    void fetchUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Sign In: User signed in.");
            currentUser = user;
            userID = currentUser.getUid();
            setSignInStatus(SUCCESS);
        } else {
            Log.d(LOG_TAG, "Sign In: Attempting sign-in anon.");
            setSignInStatus(ATTEMPTING_SIGN_IN);
            signInAnonymously();
        }
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
