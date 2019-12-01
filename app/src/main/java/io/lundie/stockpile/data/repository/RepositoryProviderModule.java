package io.lundie.stockpile.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.stocklist.additem.ImageUploadManager;
import io.lundie.stockpile.injection.ActivityScope;
import io.lundie.stockpile.utils.AppExecutors;

@Module
public class RepositoryProviderModule {

    @ActivityScope
    @Provides
    UserRepository provideUserRepository(FirebaseFirestore firebaseFirestore) {
        return new UserRepository(firebaseFirestore); }

    // Note that FireBase injection is provided through the :
    // io.lundie.stockpile.injection.ProvidersModule

    @ActivityScope
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