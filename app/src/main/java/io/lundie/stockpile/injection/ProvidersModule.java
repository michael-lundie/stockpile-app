package io.lundie.stockpile.injection;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.utils.AppExecutors;
import io.lundie.stockpile.utils.PicassoFirebaseRequestHandler;

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

    @Provides
    AppExecutors appExecutorProvider() { return  AppExecutors.getInstance(); }

    // Note: Picasso functionality is provided through BindingComponent
}
