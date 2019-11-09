package io.lundie.stockpile.data;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This class is a modified implementation of the following:
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 */
public class FirestoreQueryLiveData extends LiveData<QuerySnapshot> {

    // TODO: Remove dev logs
    private static final String LOG_TAG = FirestoreQueryLiveData.class.getSimpleName();

    private final Query query;
    private ListenerRegistration registration = null;
    private final QueryEventListener listener = new QueryEventListener();
    private boolean listenerRemovePending = false;

    private final Handler handler = new Handler();
    private final Runnable removeListener = () -> {
        registration.remove();
        listenerRemovePending = false;
    };

    public FirestoreQueryLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (listenerRemovePending) {
            // If active and listener removal was pending, cancel it.
            handler.removeCallbacks(removeListener);
        } else {
            Log.d(LOG_TAG, "LiveData onActive");
            this.registration = query.addSnapshotListener(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "LiveData onInactive");
        super.onInactive();
        // onInactive, delay removal of listener, in case of orientation change. Prevents
        // setting up the query and listener again (data loading) unnecessarily.
        //TODO: Create test for the above.
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = false;
    }

    private class QueryEventListener implements EventListener<QuerySnapshot> {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                            @Nullable FirebaseFirestoreException e) {
            if(e != null) {
                Log.w(LOG_TAG, "Listener failed", e);
                return;
            }
            Log.d(LOG_TAG, "On event triggered. Returning " + queryDocumentSnapshots.getDocuments().get(0));
            setValue(queryDocumentSnapshots);
        }
    }
}