package io.lundie.stockpile.injection;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.data.repository.RepositoryProviderModule;
import io.lundie.stockpile.features.authentication.UserViewModel;
import io.lundie.stockpile.features.authentication.di.AuthFragmentModule;
import io.lundie.stockpile.features.authentication.di.UserProviderModule;
import io.lundie.stockpile.features.authentication.di.UserViewModelModule;
import io.lundie.stockpile.features.homeview.di.HomeFragmentModule;
import io.lundie.stockpile.features.stocklist.additem.di.AddItemFragmentModule;
import io.lundie.stockpile.features.stocklist.categorylist.di.CategoryFragmentModule;
import io.lundie.stockpile.features.stocklist.item.di.ItemFragmentModule;
import io.lundie.stockpile.features.stocklist.itemlist.di.ItemListFragmentModule;

@ActivityScope
@Subcomponent( modules = {  ProvidersModule.class,
                            AuthFragmentModule.class,
                            UserProviderModule.class,
                            UserViewModelModule.class,
                            HomeFragmentModule.class,
                            ItemListFragmentModule.class,
                            CategoryFragmentModule.class,
                            ItemFragmentModule.class,
                            AddItemFragmentModule.class })
public interface MainActivitySubComponent extends AndroidInjector<MainActivity> {

    // Using new dagger factory method, as opposed to builder.
    // See docs: https://dagger.dev/android
    @Subcomponent.Factory
    interface Factory extends AndroidInjector.Factory<MainActivity> {}
}