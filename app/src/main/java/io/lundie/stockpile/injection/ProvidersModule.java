package io.lundie.stockpile.injection;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;

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
}
