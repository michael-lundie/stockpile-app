package io.lundie.stockpile.data;

import com.google.firebase.firestore.DocumentSnapshot;

public interface FirestoreLiveDataListener {
    void onEventSuccess(DocumentSnapshot documentSnapshot);
    void onEventFailure();
}
