package io.lundie.stockpile.features.authentication;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.firestore.UserData;
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
    }

    public void init() {
        if(getUserID() == null) {
            fetchUser();
        }
    }

    public String getUserID() {
        if(userID == null || !userID.isEmpty()) {
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
            setSignInStatus(SignInStatusType.REQUEST_SIGN_IN);
        }
    }

    void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                        currentUser = firebaseAuth.getCurrentUser();
                        userID = currentUser.getUid();
                        // Create a new firestore directory for the anonymous user
                        createFirestoreUserData(userID, "", "");
                    } else {
                        currentUser = null;
                        setSignInStatus(FAIL_AUTH);
                    }
                });
    }

    void authAccountWithGoogle(AuthCredential credential, String displayName,
                                   String emailAddress) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       Timber.w(task.getException(), "signInWithCredential:success");
                       currentUser = task.getResult().getUser();
                       userID = currentUser.getUid();
                        initUserAccount(userID, displayName, emailAddress);
                   } else {
                       Timber.w(task.getException(), "signInWithCredential:failure");
                       setSignInStatus(FAIL_AUTH);
                   }
                });
    }

    private void initUserAccount(String userID, String displayName, String emailAddress) {
        firestore.collection("users").document(userID).get().
                addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()) {
                            Timber.e("Init account --> data exists");
                            setSignInStatus(SUCCESS);
                        } else {
                            Timber.e("Init account --> CREATING data");
                            createFirestoreUserData(userID, displayName, emailAddress);
                        }
                    } else {
                        Timber.d(task.getException(), "Error retrieving firestore docs.");
                    }
                }
         );
    }

    private void createFirestoreUserData(String userID, String displayName, String email) {
        UserData userData = new UserData();
        userData.setDisplayName(displayName);
        userData.setEmail(email);
        userData.setUserID(userID);
        firestore.collection("users")
                .document(userID).set(userData).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                createFirestoreCategories(userID);
                if(displayName == null || displayName.isEmpty()) {
                    //TODO: replace status events with fetch user.
                    setSignInStatus(SUCCESS_ANON);
                } else {
                    setSignInStatus(SUCCESS);
                }
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

    public void signOutUser() {
        firebaseAuth.signOut();
        userID = null;
        setSignInStatus(SignInStatusType.SIGN_OUT);
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