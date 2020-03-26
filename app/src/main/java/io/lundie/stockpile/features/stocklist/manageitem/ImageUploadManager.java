package io.lundie.stockpile.features.stocklist.manageitem;

import android.app.Application;
import android.net.Uri;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static io.lundie.stockpile.features.stocklist.manageitem.ImageWorker.ITEM_NAME_KEY;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageWorker.STORE_PATH_KEY;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageWorker.URI_KEY;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageWorker.USER_ID_KEY;

/**
 * Simple implementation of Image Upload Manager
 *
 * TODO: Future implementation is observable and static. Will be able to handle multiple
 * uploads, plus a queue. Will handle resuming uploads in case use goes offline etc.
 */
public class ImageUploadManager {

    private final Application application;
    private final FirebaseStorage storage;

    @Inject
    public ImageUploadManager(Application application, FirebaseStorage firebaseStorage) {
        this.storage = firebaseStorage;
        this.application = application;
    }

    public void uploadImage(String storagePath, Uri imageUri,
                            String itemName, String userID) {

        Data data = new Data.Builder()
                .putString(STORE_PATH_KEY, storagePath)
                .putString(USER_ID_KEY, userID)
                .putString(URI_KEY, imageUri.toString())
                .putString(ITEM_NAME_KEY, itemName)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(ImageWorker.class)
                .setInputData(data)
                .addTag(itemName)
                .setInitialDelay(1, TimeUnit.SECONDS).build();

        ListenableFuture<Operation.State.SUCCESS> listenableFuture =
                WorkManager.getInstance(application).enqueue(request).getResult();

        try {
            Timber.e("ImageUploadManager: Begin try.");
            // Will wait until task is complete of an error is thrown
            listenableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            deleteImage(storagePath);
        }
    }

    public void deleteImage(String storagePath) {

        StorageReference storageRef = storage.getReference();
        StorageReference imageReference = storageRef.child(storagePath);

        imageReference.delete()
                .addOnSuccessListener(aVoid -> Timber.d("Successfully deleted image"))
                .addOnFailureListener(exception -> Timber.d(exception, "Unable to delete file."));
    }
}