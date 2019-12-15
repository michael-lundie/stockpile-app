package io.lundie.stockpile.injection;

import android.app.Application;
import android.content.ContentResolver;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.stocklist.additem.ImageUploadManager;
import io.lundie.stockpile.utils.AppExecutors;

@Module
class AppProviderModule {

    @Provides
    ContentResolver providesContentResolver(Application application) {
        return application.getContentResolver();
    }

    @Provides
    FirebaseFirestore firebaseFirestoreProvider() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    FirebaseAuth firebaseAuthProvider() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    FirebaseStorage firebaseStorageProvider() { return FirebaseStorage.getInstance(); }

    // UserManager provider is in features --> authentication --> di module

    @Provides
    ImageUploadManager providesImageUploadManager(FirebaseStorage firebaseStorage,
                                                  ContentResolver contentResolver) {
        return new ImageUploadManager(firebaseStorage, contentResolver);
    }

    @Provides
    AppExecutors appExecutorProvider() { return  AppExecutors.getInstance(); }
}