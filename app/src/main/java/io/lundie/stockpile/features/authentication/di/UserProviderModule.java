package io.lundie.stockpile.features.authentication.di;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.features.authentication.UserPrefs;
import io.lundie.stockpile.injection.AppScope;
import io.lundie.stockpile.utils.data.CategoryBuilder;

@Module
public class UserProviderModule {

    @AppScope
    @Provides
    UserManager provideUserManager(FirebaseAuth firebaseAuth, FirebaseFirestore firebaseFirestore,
                                   CategoryBuilder categoryBuilder) {
        return new UserManager(firebaseAuth, firebaseFirestore, categoryBuilder);
    }

    @AppScope
    @Provides
    UserPrefs providesUserPrefs(SharedPreferences sharedPreferences) {
        return new UserPrefs(sharedPreferences);
    }

    CategoryBuilder provideCategoryBuilder(Resources resources) {
        return new CategoryBuilder(resources);
    }
}
