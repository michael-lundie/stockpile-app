package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;

import timber.log.Timber;

abstract class BaseRepository {
    boolean isUserIDEmpty(String userID) {
        if(userID.isEmpty()) {
            Timber.e("UserID is clear. Ensure UserManager has retrieved userID.");
            return true;
        } return false;
    }

    abstract CollectionReference collectionPath(@NonNull String userID);
}
