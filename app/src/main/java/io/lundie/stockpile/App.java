package io.lundie.stockpile;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.storage.FirebaseStorage;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Cache;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.lundie.stockpile.injection.AppComponent;
import io.lundie.stockpile.injection.BindingComponent;
import io.lundie.stockpile.injection.DaggerAppComponent;
import io.lundie.stockpile.injection.DaggerBindingComponent;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import io.lundie.stockpile.utils.picasso.PicassoFirebaseRequestHandler;
import okhttp3.OkHttpClient;
import timber.log.Timber;

public class App extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppExecutors.getInstance().diskIO().execute(() ->
                AndroidThreeTen.init(getApplicationContext()));

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {

        AppComponent appComponent = DaggerAppComponent.factory().create(this);

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE))
                .addRequestHandler(new PicassoFirebaseRequestHandler(FirebaseStorage.getInstance()))
                .build();
        Picasso.setSingletonInstance(picasso);

        BindingComponent bindingComponent = DaggerBindingComponent.factory().create(picasso);
        DataBindingUtil.setDefaultComponent(bindingComponent);

        return appComponent;
    }
}
