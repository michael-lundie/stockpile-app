package io.lundie.stockpile.features.authentication;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import io.lundie.stockpile.utils.data.CategoryBuilder;
import timber.log.Timber;

import static io.lundie.stockpile.features.authentication.SignInStatusType.FAIL_AUTH;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS;
import static io.lundie.stockpile.features.authentication.SignInStatusType.SUCCESS_ANON;

public class UserManager {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final CategoryBuilder categoryBuilder;
    private FirebaseUser currentUser;
    private String userID;

    private ArrayList<SignInStatusObserver> signInStatusObservers = new ArrayList<>();
    private int observerCount;

    private @SignInStatusTypeDef int signInStatus;
    
    @Inject
    public UserManager(FirebaseAuth firebaseAuth, FirebaseFirestore firestore,
                       CategoryBuilder categoryBuilder) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;
        this.categoryBuilder = categoryBuilder;
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
            currentUser = user;
            userID = currentUser.getUid();
            Timber.d("Sign In: User signed in. ID: %s", userID);
            setSignInStatus(SUCCESS);
        } else {
            Timber.d("Sign In: Attempting sign-in anon.");
            setSignInStatus(SignInStatusType.REQUEST_SIGN_IN);
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        currentUser = firebaseAuth.getCurrentUser();
                        userID = currentUser.getUid();
                        // Create a new firestore directory for the anonymous user
                        createFirestoreUserData(userID);
                    } else {
                        currentUser = null;
                        setSignInStatus(FAIL_AUTH);
                    }
                });
    }

    private void createFirestoreUserData(String userID) {
        UserData userData = new UserData();
        userData.setDisplayName("");
        userData.setEmail("");
        userData.setUserID(userID);
        firestore.collection("users")
                .document(userID).set(userData).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                createFirestoreCategories(userID);
                setSignInStatus(SUCCESS_ANON);
            } else {
                setSignInStatus(FAIL_AUTH);
                if(task.getException() != null) {
                    Timber.e(task.getException(), "Error updating UserData");
                }
            }
        });
    }

    public void registerWithAnonymousAccount(AuthCredential credential, String displayName,
                                             String emailAddress, UserData userData) {
        if(userData != null) {
            userData.setDisplayName(displayName);
            userData.setEmail(emailAddress);
            firebaseAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Timber.d("linkWithCredential:success");
                            if(task.getResult() != null) {
                                currentUser = task.getResult().getUser();
                                updateFirestoreData(currentUser.getUid(), userData);
                            }
                        } else {
                            Timber.w(task.getException(), "linkWithCredential:failure");
                            setSignInStatus(FAIL_AUTH);
                        }
                    });
        } else {
            Timber.e("Could not update, user data was null");
            setSignInStatus(FAIL_AUTH);
        }
    }

    private void updateFirestoreData(String userID, UserData userData) {
        firestore.collection("users")
                .document(userID).set(userData).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                setSignInStatus(SUCCESS);
            } else {
                setSignInStatus(FAIL_AUTH);
                if(task.getException() != null) {
                    Timber.e(task.getException(), "Error updating UserData");
                }
            }
        });
    }

    private void createFirestoreCategories(String userID) {
        firestore.collection("users").document(userID)
                .set(categoryBuilder.getCategoryObject(), SetOptions.merge());
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