package io.lundie.stockpile.features.general.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.general.AlertDialogFragment;

@Module
public abstract class AlertDialogFragmentModule {

    @ContributesAndroidInjector()
    abstract AlertDialogFragment contributesAlertDialogFragment();
}
