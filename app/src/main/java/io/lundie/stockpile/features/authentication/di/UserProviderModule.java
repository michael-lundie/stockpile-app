package io.lundie.stockpile.features.authentication.di;

import com.google.firebase.auth.FirebaseAuth;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.injection.ActivityScope;
import io.lundie.stockpile.injection.AppScope;

@Module
public class UserProviderModule {

    @AppScope
    @Provides
    UserManager provideUserManager(FirebaseAuth firebaseAuth) {
        return new UserManager(firebaseAuth);
    }
}
