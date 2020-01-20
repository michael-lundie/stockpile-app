package io.lundie.stockpile.features.authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.ATTEMPTING_SIGN_IN;
import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS_ANON;

public class UserManager {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private static FirebaseUser currentUser;
    private static String userID;

    private ArrayList<SignInStatusObserver> signInStatusObservers = new ArrayList<>();
    private int observerCount;

    private @SignInStatusTypeDef int signInStatus;
    
    @Inject
    public UserManager(FirebaseAuth firebaseAuth, FirebaseFirestore firestore) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        fetchUser();
    }

    public String getUserID() {
        if(!userID.isEmpty()) {
            return userID;
        } return null;
    }

    public FirebaseUser getCurrentUser() {
        if(currentUser != null) {
            return currentUser;
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
            setSignInStatus(SignInStatusType.REQUEST_SIGN_IN);
//            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        if(currentUser != null) {
                            userID = currentUser.getUid();
                            // Create a new firestore directory for the anonymous user
                            UserData userData = new UserData();
                            userData.setDisplayName("");
                            userData.setUserID(userID);
                            firestore.collection("users")
                                    .document(userID).set(userData).addOnCompleteListener(task2 -> {
                                if(task2.isSuccessful()) {
                                    setSignInStatus(SUCCESS_ANON);
                                }
                            });
                        } else {
                            setSignInStatus(FAIL_AUTH);
                        }
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