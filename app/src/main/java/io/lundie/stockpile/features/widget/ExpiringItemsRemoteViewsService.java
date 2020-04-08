package io.lundie.stockpile.features.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.features.authentication.UserManager;

public class ExpiringItemsRemoteViewsService extends RemoteViewsService {

    @Inject ItemListRepository itemListRepository;

    @Inject UserManager userManager;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        AndroidInjection.inject(this);
        return new ExpiringItemsWidgetListProvider(getApplicationContext(), intent,
                itemListRepository, userManager);
    }
}
