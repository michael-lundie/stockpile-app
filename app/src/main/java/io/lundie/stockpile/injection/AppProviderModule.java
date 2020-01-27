package io.lundie.stockpile.injection;

import android.app.Application;
import android.content.ContentResolver;
import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.EventMessageController;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import io.lundie.stockpile.features.stocklist.manageitem.ImageUploadManager;
import io.lundie.stockpile.utils.AppExecutors;

@Module
class AppProviderModule {

    @Provides
    ContentResolver providesContentResolver(Application application) {
        return application.getContentResolver();
    }

    @Provides
    Resources provideResources(Application application) { return application.getResources(); }

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

    // UserManager provider is in features --> authentication --> di

    @Provides
    ImageUploadManager providesImageUploadManager(FirebaseStorage firebaseStorage,
                                                  ContentResolver contentResolver) {
        return new ImageUploadManager(firebaseStorage, contentResolver);
    }

    @Provides
    AppExecutors appExecutorProvider() { return  AppExecutors.getInstance(); }

    @AppScope
    @Provides
    ItemPileBus providesItemPileBus() {
        return new ItemPileBus();
    }

    // Note: Picasso functionality is provided through BindingComponent
    @AppScope
    @Provides
    EventMessageController providesEventMessageController() {
        return new EventMessageController();
    }
}