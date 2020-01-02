package io.lundie.stockpile.data;

public interface FirestoreLiveDataListener {
    void onSuccess();
    void onFailure();
}
