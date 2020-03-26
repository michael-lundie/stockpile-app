package io.lundie.stockpile.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreDocumentLiveData;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.features.stocklist.manageitem.AddItemStatusObserver;
import io.lundie.stockpile.features.stocklist.manageitem.ImageUploadManager;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import timber.log.Timber;

public class ItemRepository extends BaseRepository{

    private final FirebaseFirestore firestore;
    private final ImageUploadManager uploadManager;
    private final AppExecutors appExecutors;
    private final TargetsRepository targetsRepository;
    private FirestoreDocumentLiveData itemLiveData;

    @Inject
    ItemRepository(FirebaseFirestore firebaseFirestore, ImageUploadManager imageUploadManager,
                   TargetsRepository targetsRepository, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.uploadManager = imageUploadManager;
        this.appExecutors = appExecutors;
        this.targetsRepository = targetsRepository;
    }

    public LiveData<DocumentSnapshot> getItemLiveDataSnapshot(String userID, String itemName) {
        if(itemLiveData != null && itemLiveData.getValue() != null) {
            return itemLiveData;
        } else {
            return itemLiveData = new FirestoreDocumentLiveData(collectionPath(userID).document(itemName));
        }
    }

    public void getItemPile(String userID, String itemName, @NonNull getStaticItemObserver observer) {
        collectionPath(userID).document(itemName).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document!= null && document.exists()) {
                    observer.onSuccess(document.toObject(ItemPile.class));
                } else {
                    observer.onFailed();
                    Timber.d("-->> UserRepo:Firestore: No such document");
                }
            } else {
                observer.onFailed();
                Timber.d(task.getException(), "Task failed.");
            }
        });
    }

    public void addItem(String userID, ItemPile itemPile) {
        setItemPileData(userID, itemPile);
    }

    public void addItem(String userID, String uri, ItemPile itemPile) {

        String itemName = itemPile.getItemName();

        String storagePath = createImageStoragePath(userID, itemName);
        itemPile.setImagePath(storagePath);
        setItemPileData(userID, itemPile);

        if(uri != null) {
            Timber.e("Repository: Sending upload to --> ImageUploadManager");
            uploadImage(uri, itemPile.getItemName(), storagePath, userID, null);
        }
    }

    public void updateItem(String userID, ItemPile updatedItemPile, String initialDocumentName) {
        setItemPileData(userID, updatedItemPile, initialDocumentName);
    }

    /**
     * This method is to be used when updating an {@link ItemPile}. An original document name
     * should be provided, so on the event that it is edited, it may be changed.
     * Because item name is used as the field ID, the previous entry would remain and thus, must be
     * deleted.
     * @param userID ID of logged in user.
     * @param uri URI of image to be uploaded
     * @param updatedItemPile {@link ItemPile} data to be updated.
     * @param initialDocumentName Original item pile name.
     */
    public void updateItemWithImageChange(String userID, String uri, ItemPile updatedItemPile,
                                          String initialDocumentName) {
        String previousImagePath = updatedItemPile.getImagePath();
        String storagePath = createImageStoragePath(userID, updatedItemPile.getItemName());
        updatedItemPile.setImagePath(storagePath);

        setItemPileData(userID, updatedItemPile, initialDocumentName);

        if(uri != null) {
            uploadImage(uri, updatedItemPile.getItemName(), storagePath,
                    userID, previousImagePath);
        }
    }

    private String createImageStoragePath(String userID, String itemName) {
        return "users/" + userID + "/" + itemName.toLowerCase() + ".jpg";
    }

    private void setItemPileData(String userID, ItemPile itemPile) {
        collectionPath(userID).document(itemPile.getItemName()).set(itemPile);
    }

    private void setItemPileData(String userID, ItemPile updatedItemPile, String originalItemName) {
        setItemPileData(userID, updatedItemPile);
        if(originalItemName != null && !originalItemName.isEmpty()
                && !originalItemName.equals(updatedItemPile.getItemName()) ) {
            // Name changed, so remove ItemPile with old name
            deleteItemPile(userID, originalItemName);
        }
    }

    /**
     * Deletes a document from the items firestore collection.
     * @param userID should correspond to currently signed in users ID
     * @param itemPileName the document name (docID) of the document to be deleted
     */
    public void deleteItemPile(String userID, String itemPileName) {
        collectionPath(userID).document(itemPileName).delete();
    }

    /**
     * Initialises uploading an image to firebase storage via the {@link ImageUploadManager}
     * and posts the status result to an {@link AddItemStatusObserver}.
     * @param uri The uri that our resulting uploaded image should be accessible from
     * @param storagePath the current signed in users storage path
     * @param userID User ID of the currently logged in user
     * @param removeImagePath
     */
    private void uploadImage(String uri, String itemName,
                             String storagePath, String userID,
                             @Nullable String removeImagePath) {
        uploadManager.uploadImage(storagePath, Uri.parse(uri), itemName, userID);
        if(removeImagePath != null && !removeImagePath.isEmpty()) {
            deleteImage(removeImagePath);
        }
    }

    private void deleteImage(String storagePath) {
        appExecutors.networkIO().execute(() -> uploadManager.deleteImage(storagePath));
    }

    /**
     * Simple method when removes the generated imageURI from firestore in the case
     * our item did not upload correctly.
     * @param documentReference
     */
    private void removeUriFromCollection(DocumentReference documentReference) {
        documentReference.update("imageURI", FieldValue.delete());
    }

    @Override
    CollectionReference collectionPath(@NonNull String userID) {
        return firestore.collection("users")
                .document(userID).collection("items");
    }

    public interface getStaticItemObserver {
        void onSuccess(ItemPile itemPile);
        void onFailed();
    }
}