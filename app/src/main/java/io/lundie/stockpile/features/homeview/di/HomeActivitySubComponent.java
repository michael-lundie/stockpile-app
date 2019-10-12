package io.lundie.stockpile.features.homeview;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface HomeActivitySubComponent extends AndroidInjector<HomeActivity> {

    // Using new dagger factory method, as opposed to builder.
    // See docs: https://dagger.dev/android
    @Subcomponent.Factory
    interface Factory extends AndroidInjector.Factory<HomeActivity> {}
}
