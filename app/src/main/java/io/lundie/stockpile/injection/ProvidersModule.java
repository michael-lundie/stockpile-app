package io.lundie.stockpile.injection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
    AppExecutors appExecutorProvider() { return  AppExecutors.getInstance(); }
}
