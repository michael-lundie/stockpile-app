package io.lundie.stockpile.injection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.utils.AppExecutors;

@Module
class ProvidersModule {

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
    AppExecutors appExecutorProvider() { return  AppExecutors.getInstance(); }

    // Note: Picasso functionality is provided through BindingComponent
}
