package io.lundie.stockpile.injection;

import android.app.Application;
import android.content.ContentResolver;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import io.lundie.stockpile.features.EventMessageController;
import io.lundie.stockpile.features.stocklist.additem.ImageUploadManager;
import io.lundie.stockpile.utils.AppExecutors;

@Module
class ProvidersModule {
    // Note: Picasso functionality is provided through BindingComponent
    @ActivityScope
    @Provides
    EventMessageController providesEventMessageController() {
        return new EventMessageController();
    }
}
