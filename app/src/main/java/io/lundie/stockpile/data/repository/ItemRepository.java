package io.lundie.stockpile.data.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

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
    private final ImageUploadManager uploadManager;
    private final AppExecutors appExecutors;

    FirestoreDocumentLiveData itemPileLiveData;

    @Inject
    ItemRepository(FirebaseFirestore firebaseFirestore, ImageUploadManager imageUploadManager,
                   AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.uploadManager = imageUploadManager;
        this.appExecutors = appExecutors;
    }

    public LiveData<DocumentSnapshot> getItemDocumentSnapshotLiveData() { return  itemPileLiveData; }

    public void setItem(String userID, String uri, int changeInCalories, ItemPile itemPile,
                        AddItemStatusObserver observer) {

        String itemName = itemPile.getItemName();

        String storagePath = "users/" + userID + "/" + itemName.toLowerCase() + ".jpg";
        itemPile.setImageURI(storagePath);

        DocumentReference documentReference = firestore.collection("users")
                .document(userID).collection("items")
                .document(itemName);

        documentReference
                .set(itemPile)
                .addOnSuccessListener(aVoid -> {

                    if(uri != null) {
                        uploadImage(uri, observer, storagePath, documentReference);
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

    public void setItem(String userID, String uri, int changeInCalories, ItemPile itemPile,
                        String initialDocName, AddItemStatusObserver observer) {

        String itemName = itemPile.getItemName();

        AtomicBoolean hasItemNameChanged = new AtomicBoolean(false);
        if(initialDocName != null && !initialDocName.isEmpty()) {
            if(!initialDocName.equals(itemName)) hasItemNameChanged.set(true);
        }

        String storagePath = "users/" + userID + "/" + itemName.toLowerCase() + ".jpg";
        itemPile.setImageURI(storagePath);

        DocumentReference documentReference = firestore.collection("users")
                        .document(userID).collection("items")
                        .document(itemName);

        documentReference
                .set(itemPile)
                .addOnSuccessListener(aVoid -> {
                    if(uri != null) {
                        uploadImage(uri, observer, storagePath, documentReference);
                    } else {
                        // uri was null
                        observer.update(SUCCESS_NO_IMAGE);
                    }
                    // If name changed, firestore 'set' will add an entirely new item entry.
                    // As such, we must delete the previous document.
                    if(hasItemNameChanged.get()) removeItem(userID, initialDocName);
//                    if(hasExpiryListChanged.get()) resyncExpiryData(userID, itemName, initialExpiryList, itemPile.getExpiry());
                })
                .addOnFailureListener(error -> {
                    Timber.e(error, "Error adding document.");
                    observer.update(FAILED);
                });
    }

    private void updateCategoryCaloriesTotal(String userID) {
        DocumentReference documentReference = firestore.collection("users").document(userID);
    }

    /**
     * Deletes a document from the items firestore collection.
     * @param userID should correspond to currently signed in users ID
     * @param documentName the document name (docID) of the document to be deleted
     */
    private void removeItem(String userID, String documentName) {
        firestore.collection("users")
                .document(userID).collection("items")
                .document(documentName)
                .delete();
    }

    /**
     * Initialises uploading an image to firebase storage via the {@link ImageUploadManager}
     * and posts the status result to an {@link AddItemStatusObserver}.
     * @param uri The uri that our resulting uploaded image should be accessible from
     * @param observer {@link AddItemStatusObserver}
     * @param storagePath the current signed in users storage path
     * @param documentReference {@link DocumentReference} of the current document being uploaded
     */
    private void uploadImage(String uri, AddItemStatusObserver observer,
                             String storagePath, DocumentReference documentReference) {
        appExecutors.networkIO().execute(() ->
                uploadManager.uploadImage(storagePath, Uri.parse(uri), isSuccessful -> {
            if(isSuccessful) {
                observer.update(SUCCESS);
            } else {
                observer.update(IMAGE_FAILED);
                removeUriFromCollection(documentReference);
            }
        }));
    }

    /**
     * Simple method when removes the generated imageURI from firestore in the case
     * our item did not upload correctly.
     * @param documentReference
     */
    private void removeUriFromCollection(DocumentReference documentReference) {
        documentReference.update("imageURI", FieldValue.delete());
    }
}