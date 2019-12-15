package io.lundie.stockpile.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.stocklist.additem.ImageUploadManager;
import io.lundie.stockpile.injection.ActivityScope;
import io.lundie.stockpile.injection.AppScope;
import io.lundie.stockpile.utils.AppExecutors;

/**
 * SCOPE: Note that {@link RepositoryProviderModule} has Application Scope in order to protect
 * certain repository data on rotation (limiting the amount of fetch requests to FireStore).
 * An alternative solution may be local caching - allowing more flexibility.
 */
@Module
public class RepositoryProviderModule {

    @AppScope
    @Provides
    UserRepository provideUserRepository(FirebaseFirestore firebaseFirestore) {
        return new UserRepository(firebaseFirestore); }

    // Note that FireBase injection is provided through the :
    // io.lundie.stockpile.injection.ProvidersModule

    @AppScope
    @Provides
    ItemListRepository provideItemListRepository(FirebaseFirestore firebaseFirestore,
                                                 FirebaseStorage firebaseStorage) {
        return new ItemListRepository(firebaseFirestore, firebaseStorage); }

    @Provides
    ItemRepository provideItemRepository(FirebaseFirestore firebaseFirestore,
                                         FirebaseStorage firebaseStorage,
                                         ImageUploadManager imageUploadManager,
                                         AppExecutors appExecutors) {
        return new ItemRepository(firebaseFirestore, firebaseStorage,
                                imageUploadManager, appExecutors); }
}