package io.lundie.stockpile;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.lundie.stockpile.injection.DaggerAppComponent;

public class App extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.factory().create(this);
    }
}
