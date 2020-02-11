package io.lundie.stockpile.features.stocklist.manageitem;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.lundie.stockpile.utils.BooleanStatusObserver;

/**
 * Simple implementation of Image Upload Manager
 *
 * TODO: Future implementation is observable and static. Will be able to handle multiple
 * uploads, plus a queue. Will handle resuming uploads in case use goes offline etc.
 */
public class ImageUploadManager {

    private static final String LOG_TAG = ImageUploadManager.class.getSimpleName();

    private final ContentResolver contentResolver;
    private final FirebaseStorage storage;

    @Inject
    public ImageUploadManager(FirebaseStorage firebaseStorage, ContentResolver contentResolver) {
        this.storage = firebaseStorage;
        this.contentResolver = contentResolver;
    }

    public void uploadImage(String storagePath, Uri imageUri, BooleanStatusObserver observer) {

        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            observer.update(false);
            return; //Return false;
        }

        //TODO: Remove debug logs
        StorageReference storageRef = storage.getReference();

        Log.d(LOG_TAG, "Upload: Path: " + storagePath);
        StorageReference testImageRef = storageRef.child(storagePath);

        Log.e(LOG_TAG, "BItmap status : " + bitmap);
        if(bitmap != null) {

            bitmap = processBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();

            UploadTask uploadTask = testImageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                Log.d(LOG_TAG, "Failure Uploading to storage: " + exception);
                observer.update(false); })
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(LOG_TAG, "Success: Looks like Upload was successful!");
                        observer.update(true); });
        }
    }

    private Bitmap processBitmap(Bitmap bitmap) {

        Bitmap resizedBitmap;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        Log.e(LOG_TAG, "Bitmap width : " + bitmapWidth + " Height: " + bitmapHeight);

        int targetWidth = 1024;
        int targetHeight;
        if(bitmapWidth > targetWidth) {
            targetHeight = (int) (bitmapHeight * (targetWidth / (double) bitmapWidth));
            float scaleWidth = targetWidth / (float) bitmapWidth;
            float scaleHeight = targetHeight / (float) bitmapHeight;
            Log.e(LOG_TAG, "Scale Width: " + scaleWidth + " Scale Height: " + scaleHeight);
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Log.e(LOG_TAG, "New Width: " + targetWidth + " New Height: " + targetHeight);

            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            bitmap.recycle();
            return resizedBitmap;
        } return bitmap;
    }
}
