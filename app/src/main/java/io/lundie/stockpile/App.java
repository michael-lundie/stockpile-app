package io.lundie.stockpile;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.storage.FirebaseStorage;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Picasso;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.lundie.stockpile.injection.AppComponent;
import io.lundie.stockpile.injection.BindingComponent;
import io.lundie.stockpile.injection.DaggerAppComponent;
import io.lundie.stockpile.injection.DaggerBindingComponent;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import io.lundie.stockpile.utils.PicassoFirebaseRequestHandler;
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
                .addRequestHandler(new PicassoFirebaseRequestHandler(FirebaseStorage.getInstance()))
                .build();
        Picasso.setSingletonInstance(picasso);

        BindingComponent bindingComponent = DaggerBindingComponent.factory().create(picasso);
        DataBindingUtil.setDefaultComponent(bindingComponent);

        return appComponent;
    }
}
