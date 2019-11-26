package io.lundie.stockpile.features.authentication;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;

import static io.lundie.stockpile.features.authentication.SignInStatusType.ATTEMPTING_SIGN_IN;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS_ANON;
import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;

public class UserManager {

    private static final String LOG_TAG = UserManager.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userID;

    private ArrayList<SignInStatusObserver> signInStatusObservers = new ArrayList<>();
    private int observerCount;

    @SignInStatusTypeDef int signInStatus;
    
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

    //TODO: How does firebase auth handle offline cases?

    private void fetchUser() {
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

    public void addObserver(SignInStatusObserver observer) {
        //TODO: Manage Sign-in error states
        this.signInStatusObservers.add(observer);
        observerCount ++;
        observer.update(signInStatus);
        Log.e(LOG_TAG, "UserManager: ADDING Observers: " + observerCount);
    }

    public void removeObserver(SignInStatusObserver observer) {
        this.signInStatusObservers.remove(observer);
        observerCount --;
        trimObserverArray();
        Log.e(LOG_TAG, "UserManager: REMOVING Observers: " + observerCount);
    }

    private void trimObserverArray() {
        if(observerCount == 0) {
            Log.e(LOG_TAG, "UserManager: trimming");
            signInStatusObservers.trimToSize();
        }
    }

    private void setSignInStatus(@SignInStatusTypeDef int status) {
        Log.e(LOG_TAG, "UserManager: Setting SignIn. Status = " + status);
        this.signInStatus = status;
        Log.e(LOG_TAG, "UserManager: Observers: Set : " + observerCount);
        for(SignInStatusObserver observer : this.signInStatusObservers) {
            Log.e(LOG_TAG, "UserManager: Looping updates");
            observer.update(status);
        }
    }
}
