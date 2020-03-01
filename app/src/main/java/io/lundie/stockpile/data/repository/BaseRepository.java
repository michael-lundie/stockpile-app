package io.lundie.stockpile.data.repository;

import timber.log.Timber;

class BaseRepository {
    boolean isUserIDEmpty(String userID) {
        if(userID.isEmpty()) {
            Timber.e("UserID is empty. Ensure UserManager has retrieved userID.");
            return true;
        } return false;
    }
}
