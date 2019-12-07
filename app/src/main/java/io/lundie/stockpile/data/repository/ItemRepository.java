package io.lundie.stockpile.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.features.stocklist.additem.AddItemStatusObserver;
import io.lundie.stockpile.features.stocklist.additem.ImageUploadManager;
import io.lundie.stockpile.utils.AppExecutors;

import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.FAILED;
import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.IMAGE_FAILED;
import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.SUCCESS;

public class ItemRepository{

    private static final String LOG_TAG = ItemRepository.class.getSimpleName();

    MutableLiveData<String> testLiveData = new MutableLiveData<>();

    private final FirebaseFirestore firestore;
    private final FirebaseStorage storage;
    private final ImageUploadManager uploadManager;
    private final AppExecutors appExecutors;

    @Inject
    ItemRepository(FirebaseFirestore firebaseFirestore, FirebaseStorage firebaseStorage,
                   ImageUploadManager imageUploadManager, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.storage = firebaseStorage;
        this.uploadManager = imageUploadManager;
        this.appExecutors = appExecutors;
        Log.i(LOG_TAG, "-->> ItemRepository: initialising. <<--");
    }

    public LiveData<String> getTestLiveData() {
        if(testLiveData != null) {
            testLiveData.setValue("TEST LIVE DATA");
        }
        return testLiveData;
    }

    public void addItem(String userID, String uri, ItemPile itemPile, AddItemStatusObserver observer) {

        String itemName = itemPile.getItemName();
        //TODO: Make sure cat name has only alphanumeric characters
        String storagePath = "users/" + userID + "/" + itemName.toLowerCase() + ".jpg";
        itemPile.setImageURI(storagePath);

        firestore.collection("users").document(userID).collection("items")
                .document(itemName)
                .set(itemPile)
                .addOnSuccessListener(aVoid -> {
                    Log.e(LOG_TAG, "Success Doc Upload");
                    uploadImage(uri, observer, storagePath);
                })
                .addOnFailureListener(error -> {
                    Log.e(LOG_TAG, "Error adding document " + error);
                    observer.update(FAILED);
                });
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
