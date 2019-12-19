package io.lundie.stockpile.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreDocumentLiveData;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusObserver;
import io.lundie.stockpile.features.stocklist.manageitem.ImageUploadManager;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.IMAGE_FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.SUCCESS;
import static io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusType.SUCCESS_NO_IMAGE;

public class ItemRepository{

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final ImageUploadManager uploadManager;
    private final AppExecutors appExecutors;

    FirestoreDocumentLiveData itemPileLiveData;

    @Inject
    ItemRepository(FirebaseFirestore firebaseFirestore, FirebaseStorage firebaseStorage,
                   ImageUploadManager imageUploadManager, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.storage = firebaseStorage;
        this.uploadManager = imageUploadManager;
        this.appExecutors = appExecutors;
    }

    public LiveData<DocumentSnapshot> getItemDocumentSnapshotLiveData() { return  itemPileLiveData; }

    public void fetchItemPile(@NonNull String userID, @NonNull String itemName) {
        DocumentReference documentReference = firestore.collection("users").document(userID)
                .collection("items").document(itemName);
        itemPileLiveData = new FirestoreDocumentLiveData(documentReference);
    }

    public void addItem(String userID, String uri, ItemPile itemPile, AddItemStatusObserver observer) {

        String itemName = itemPile.getItemName();
        String storagePath = "users/" + userID + "/" + itemName.toLowerCase() + ".jpg";
        itemPile.setImageURI(storagePath);

        firestore.collection("users").document(userID).collection("items")
                .document(itemName)
                .set(itemPile)
                .addOnSuccessListener(aVoid -> {
                    if(uri != null) {
                        uploadImage(uri, observer, storagePath);
                    } else {
                        // uri was null
                        observer.update(SUCCESS_NO_IMAGE);
                    }
                })
                .addOnFailureListener(error -> {
                    Timber.e(error, "Error adding document.");
                    observer.update(FAILED);
                });
    }

    public void editItem() {

    }
    private void uploadImage(String uri, AddItemStatusObserver observer, String storagePath) {
        appExecutors.networkIO().execute(() ->
                uploadManager.uploadImage(storagePath, Uri.parse(uri), isSuccessful -> {
            if(isSuccessful) {
                observer.update(SUCCESS);
            } else {
                observer.update(IMAGE_FAILED);
            }
        }));
    }
}