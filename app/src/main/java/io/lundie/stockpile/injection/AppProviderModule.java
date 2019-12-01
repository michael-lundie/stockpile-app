package io.lundie.stockpile.injection;

import android.app.Application;
import android.content.ContentResolver;

import dagger.Module;
import dagger.Provides;

@Module
public class AppProviderModule {

    @Provides
    public ContentResolver providesContentResolver(Application application) {
        return application.getContentResolver();
    }
}
