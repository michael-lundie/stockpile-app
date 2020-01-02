package io.lundie.stockpile.data;

import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import timber.log.Timber;

/**
 * This class is a modified implementation of the following:
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 */
public class FirestoreDocumentLiveData extends LiveData<DocumentSnapshot> {

    private final DocumentReference reference;
    private ListenerRegistration registration = null;
    private final DocumentEventListener docEventListener = new DocumentEventListener();
    private boolean listenerRemovePending = false;
    private FirestoreLiveDataListener liveDataListener;

    private final Handler handler = new Handler();
    private final Runnable removeListener = () -> {
        registration.remove();
        listenerRemovePending = false;
    };

    public FirestoreDocumentLiveData(DocumentReference reference) {
        this.reference = reference;
    }

    public FirestoreDocumentLiveData(DocumentReference reference, FirestoreLiveDataListener liveDataListener) {
        Timber.i("UserData --> Creating DocumentLiveData!");
        this.reference = reference;
        this.liveDataListener = liveDataListener;
    }

    @Override
    protected void onActive() {
        super.onActive();
        Timber.i("UserData --> On active!");
        if (listenerRemovePending) {
            // If active and docEventListener removal was pending, cancel it.
            handler.removeCallbacks(removeListener);
        } else {
            this.registration = reference.addSnapshotListener(docEventListener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Timber.i("UserData --> On IN-active!");
        // onInactive, delay removal of docEventListener, in case of orientation change. Prevents
        // setting up the query and docEventListener again (data loading) unnecessarily.
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = false;
    }

    private class DocumentEventListener implements EventListener<DocumentSnapshot> {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                            @Nullable FirebaseFirestoreException e) {
            if(e != null) {
                Timber.e(e, "UserData --> Firestore Error reported by DocumentEventListener");
                if(liveDataListener != null) {
                    liveDataListener.onFailure();
                }
                return;
            }

            setValue(documentSnapshot);

            if(liveDataListener != null) {
                liveDataListener.onSuccess();
                Timber.i(" UserData --> Calling success FirestoreLiveData");
            }
        }
    }
}