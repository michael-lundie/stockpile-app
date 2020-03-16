package io.lundie.stockpile.injection;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.features.authentication.di.AuthFragmentModule;
import io.lundie.stockpile.features.general.di.AlertDialogFragmentModule;
import io.lundie.stockpile.features.homeview.di.HomeFragmentModule;
import io.lundie.stockpile.features.stocklist.categorylist.di.CategoryFragmentModule;
import io.lundie.stockpile.features.stocklist.item.di.ItemFragmentModule;
import io.lundie.stockpile.features.stocklist.itemlist.di.ItemListFragmentModule;
import io.lundie.stockpile.features.stocklist.manageitem.di.AddItemFragmentModule;
import io.lundie.stockpile.features.targets.di.TargetsFragmentModule;

@MainActivityScope
@Subcomponent( modules = {  AuthFragmentModule.class,
                            HomeFragmentModule.class,
                            ItemListFragmentModule.class,
                            CategoryFragmentModule.class,
                            ItemFragmentModule.class,
                            AddItemFragmentModule.class,
                            TargetsFragmentModule.class,
                            AlertDialogFragmentModule.class})
public interface MainActivitySubComponent extends AndroidInjector<MainActivity> {

    // Using new dagger factory method, as opposed to builder.
    // See docs: https://dagger.dev/android
    @Subcomponent.Factory
    interface Factory extends AndroidInjector.Factory<MainActivity> {}
}