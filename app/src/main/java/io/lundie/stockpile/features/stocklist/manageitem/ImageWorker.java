package io.lundie.stockpile.features.stocklist.manageitem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import io.lundie.stockpile.R;

import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.AVAILABLE;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.FAILED;
import static io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.ImageUpdateStatusTypeDef;

public class ImageWorker extends Worker {

    final static String STORE_PATH_KEY = "storagePath";
    final static String USER_ID_KEY = "userID";
    final static String URI_KEY = "imageURI";
    final static String ITEM_NAME_KEY = "tag";

    private final FirebaseStorage storage;
    private final Context context;
    private final SharedPreferences sharedPrefs;
    private final FirebaseFirestore firestore;

    public ImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        String storagePath = getInputData().getString(STORE_PATH_KEY);
        String imageUri = getInputData().getString(URI_KEY);
        String userID = getInputData().getString(USER_ID_KEY);
        String itemName = getInputData().getString(ITEM_NAME_KEY);
        DocumentReference docRef = buildDocumentReference(userID, itemName);

        if(storagePath == null || imageUri == null) {
            postFailureNotification(docRef);
            return Result.failure();
        }

        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                    Uri.parse(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure(); //Return false;
        }

        StorageReference storageRef = storage.getReference();
        StorageReference imageReference = storageRef.child(storagePath);

        if(bitmap != null) {
            bitmap = processBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();


            UploadTask uploadTask = imageReference.putBytes(data);

            try {
                Tasks.await(uploadTask);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                postFailureNotification(docRef);
                return Result.failure();
            }
            postSuccessNotification(docRef);
            return Result.success();
        }
        postFailureNotification(docRef);
        return Result.failure();
    }

    private DocumentReference buildDocumentReference(String userID, String itemName) {
        return firestore.collection("users").document(userID)
                .collection("items").document(itemName);
    }

    private void postSuccessNotification(DocumentReference documentReference) {
        updateDocumentImageStatus(documentReference, AVAILABLE);
        postNotification(context.getResources().getString(R.string.notify_title_success),
                context.getResources().getString(R.string.notify_label_success));
    }

    private void postFailureNotification(DocumentReference documentReference) {
        updateDocumentImageStatus(documentReference, FAILED);
        postNotification(context.getResources().getString(R.string.notify_title_fail),
                context.getResources().getString(R.string.notify_label_fail));
    }

    private Bitmap processBitmap(Bitmap bitmap) {

        Bitmap resizedBitmap;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int targetWidth = 1024;
        int targetHeight;
        if (bitmapWidth > targetWidth) {
            targetHeight = (int) (bitmapHeight * (targetWidth / (double) bitmapWidth));
            float scaleWidth = targetWidth / (float) bitmapWidth;
            float scaleHeight = targetHeight / (float) bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            bitmap.recycle();
            return resizedBitmap;
        }
        return bitmap;
    }

    /**
     *
     * Note: method modified from this blog post:
     * https://inducesmile.com/android-programming/how-to-use-android-workmanager-to-upload-image-in-background/
     * @param notificationTitle
     * @param taskStatus
     */
    private void postNotification(String notificationTitle, String taskStatus){

        String name = context.getResources().getString(R.string.app_name);
        String id = name + 31458;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),id)
                .setContentTitle(notificationTitle)
                .setContentText(taskStatus)
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(1, notification.build());
    }

    private void updateDocumentImageStatus(DocumentReference documentReference,
                                                  @ImageUpdateStatusTypeDef String status) {
        documentReference.update("imageStatus", status);
    }
}