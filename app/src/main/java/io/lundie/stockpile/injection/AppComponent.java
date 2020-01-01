package io.lundie.stockpile.injection;

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import io.lundie.stockpile.App;
import io.lundie.stockpile.data.repository.RepositoryProviderModule;
import io.lundie.stockpile.features.authentication.di.UserProviderModule;

@AppScope
@Component(modules = {  AndroidInjectionModule.class,
                        RepositoryProviderModule.class,
                        ViewModelFactoryModule.class,
                        AppProviderModule.class,
                        UserProviderModule.class,
                        MainActivityBuilder.class})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }
}