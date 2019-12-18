package io.lundie.stockpile.features.authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.ATTEMPTING_SIGN_IN;
import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS_ANON;

public class UserManager {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String userID;

    private ArrayList<SignInStatusObserver> signInStatusObservers = new ArrayList<>();
    private int observerCount;

    private @SignInStatusTypeDef int signInStatus;
    
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
            Timber.d("Sign In: User signed in.");
            currentUser = user;
            userID = currentUser.getUid();
            setSignInStatus(SUCCESS);
        } else {
            Timber.d("Sign In: Attempting sign-in anon.");
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
        Timber.e("UserManager: ADDING Observers: %s", observerCount);
    }

    public void removeObserver(SignInStatusObserver observer) {
        this.signInStatusObservers.remove(observer);
        observerCount --;
        trimObserverArray();
        Timber.e("UserManager: REMOVING Observers: %s", observerCount);
    }

    private void trimObserverArray() {
        if(observerCount == 0) {
            Timber.e("UserManager: trimming");
            signInStatusObservers.trimToSize();
        }
    }

    private void setSignInStatus(@SignInStatusTypeDef int status) {
        Timber.e("UserManager: Setting SignIn. Status = %s", status);
        this.signInStatus = status;
        Timber.e("UserManager: Observers: Set : %s", observerCount);
        for(SignInStatusObserver observer : this.signInStatusObservers) {
            Timber.e("UserManager: Looping updates");
            observer.update(status);
        }
    }
}
