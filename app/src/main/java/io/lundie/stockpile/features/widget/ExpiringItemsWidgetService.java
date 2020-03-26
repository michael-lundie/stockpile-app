package io.lundie.stockpile.features.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ExpiringItemsWidgetService extends RemoteViewsService {

    public final static String ITEM_LIST = "item_list";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ExpiringItemsWidgetListProvider(getApplicationContext(), intent);
    }
}
