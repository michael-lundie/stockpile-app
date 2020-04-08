package io.lundie.stockpile.features.widget.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import io.lundie.stockpile.features.widget.ExpiringItemsRemoteViewsService;
import io.lundie.stockpile.features.widget.ExpiringItemsWidgetService;

@Module
public abstract class ExpiringItemsWidgetProviderModule {
    @ContributesAndroidInjector()
    abstract ExpiringItemsWidgetService contributesExpiringItemsWidgetService();

    @ContributesAndroidInjector()
    abstract ExpiringItemsRemoteViewsService contributesExpiringItemsRemoteViewsService();
}
