package io.lundie.stockpile.injection;

import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.TransactionStatusController;
import io.lundie.stockpile.features.homeview.TargetListBus;
import io.lundie.stockpile.features.stocklist.ItemPileBus;
import io.lundie.stockpile.features.stocklist.manageitem.ImageUploadManager;
import io.lundie.stockpile.features.targets.TargetBus;
import io.lundie.stockpile.utils.AppExecutors;
import io.lundie.stockpile.utils.DataUtils;
import io.lundie.stockpile.utils.Prefs;

@Module
class AppProviderModule {

    @Provides
    ContentResolver providesContentResolver(Application application) {
        return application.getContentResolver();
    }

    @Provides
    Resources provideResources(Application application) { return application.getResources(); }

    @Provides
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        return builder.setDateFormat("yyyy-MM-dd HH:mm:ss:S").create();
    }

    @Provides
    DataUtils providesDataUtils(Gson gson) {
        return new DataUtils(gson);
    }

    @Provides
    Prefs providePrefs(SharedPreferences sharedPreferences) {
        return new Prefs(sharedPreferences);
    }

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

    @AppScope
    @Provides
    TargetListBus providesTargetListBus() { return new TargetListBus(); }

    @AppScope
    @Provides
    TargetBus providesTargetBus() { return new TargetBus(); }

    // Note: Picasso functionality is provided through BindingComponent
    @AppScope
    @Provides
    TransactionStatusController providesTransactionStatusController() {
        return new TransactionStatusController();
    }
}