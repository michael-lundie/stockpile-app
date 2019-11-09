package io.lundie.stockpile.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.injection.ActivityScope;

@Module
public class RepositoryProviderModule {

    @ActivityScope
    @Provides
    UserRepository provideUserRepository(FirebaseFirestore firebaseFirestore) {
        return  new UserRepository(firebaseFirestore); }

    // Note that FireBase injection is provided through the :
    // io.lundie.stockpile.injection.ProvidersModule
    @Provides
    CategoryRepository provideCategoryRepository(FirebaseFirestore firebaseFirestore) {
        return  new CategoryRepository(firebaseFirestore); }

    @ActivityScope
    @Provides
    ItemListRepository provideItemListRepository(FirebaseFirestore firebaseFirestore,
                                                 FirebaseStorage firebaseStorage) {
        return  new ItemListRepository(firebaseFirestore, firebaseStorage); }
}