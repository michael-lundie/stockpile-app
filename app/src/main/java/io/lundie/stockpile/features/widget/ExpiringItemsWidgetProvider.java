package io.lundie.stockpile.features.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.utils.DataUtils;
import io.lundie.stockpile.utils.Prefs;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import io.lundie.stockpile.utils.threadpool.CallbackRunnable;
import io.lundie.stockpile.utils.threadpool.RunnableInterface;
import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class ExpiringItemsWidgetProvider extends AppWidgetProvider {

    public final static String ACTION_UPDATE_EXPIRING_ITEMS = "update_widget_items";
    public final static String ITEMS_DATA = "item_data";

    DataUtils dataUtils;
    Prefs prefs;
    ArrayList<ItemPile> itemPiles;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.e("Broadcast: onReceive:");


        if(intent.getAction() != null) {
            Timber.e("Broadcast: onReceive: INTENT not NULL");

//            if (ACTION_UPDATE_EXPIRING_ITEMS.equals(intent.getAction())) {
                Timber.e("Broadcast: onReceive expiring items intent:");
            ArrayList<ItemPile> itemPiles = intent.getParcelableArrayListExtra(ITEMS_DATA);
            if(itemPiles != null) {
                    Timber.e("Broadcast: onReceive: %s", itemPiles.size());
                    AppExecutors.getInstance().diskIO().execute(new CallbackRunnable(new RunnableInterface() {
                        @Override
                        public void onRunCompletion() {
                            Timber.e("Broadcast: updating widget");
                            updateWidget(context);
                        }
                    }) {
                        @Override
                        public void run() {
                            String itemPileJson = getDataUtils().serializeItemPileArrayToJson(itemPiles);
                            getPrefs(context).setItemPilesForWidget(itemPileJson);
                            super.run();
                        }
                    }
                    );
                }

//            }
        } else {
            Timber.e("Broadcast: onReceive: INTENT NULL");
        }
        super.onReceive(context, intent);
    }

    private void updateWidget(Context context) {
        ComponentName appWidget = new ComponentName(context, ExpiringItemsWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(appWidget);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.e("Broadcast: onUpdate:");
        AppExecutors.getInstance().diskIO().execute(new CallbackRunnable(new RunnableInterface() {
            @Override
            public void onRunCompletion() {
                Timber.e("Broadcast: Run completed");
                int expiringItemsTotal = 0;
                if (itemPiles != null) {
                Timber.e("Broadcast: item pile not null");
                    expiringItemsTotal = itemPiles.size();
                }
                String widgetTitle = context.getResources().getString(R.string.widget_title) + expiringItemsTotal;
                // There may be multiple widgets active, so update all of them
                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, widgetTitle, itemPiles);
                }
            }
        }) {
            @Override
            public void run() {
                Timber.e("Broadcast: Running Update");
                itemPiles = getDataUtils().deserializeToItemPileArray(getPrefs(context)
                                .getItemPilesForWidgetJson());
                super.run();
            }
        }
        );
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String widgetTitle, ArrayList<ItemPile> items) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_expiring_items_provider);

        Intent intent = new Intent(context, ExpiringItemsWidgetService.class);
        Timber.e("Broadcast: updateAppWidget --> items: %s", items.size());
        intent.putParcelableArrayListExtra(ExpiringItemsWidgetService.ITEM_LIST, items);
        views.setTextViewText(R.id.widget_title, widgetTitle);
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

        // Update Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private Prefs getPrefs(Context context) {
        if(prefs == null) {
            prefs = new Prefs(PreferenceManager.getDefaultSharedPreferences(context));
        }
        return prefs;
    }

    private DataUtils getDataUtils() {
        if(dataUtils == null) {
            dataUtils = new DataUtils();
        }
        return dataUtils;
    }
}