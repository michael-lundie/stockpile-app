package io.lundie.stockpile.features.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;

import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import timber.log.Timber;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Implementation of App Widget functionality.
 */
public class ExpiringItemsWidgetProvider extends AppWidgetProvider {

    public final static String APP_WIDGET_EXPIRING_ID = "app_widget_expiring_id";
    public final static String ACTION_UPDATE_EXPIRING_ITEMS = "update_widget_items";
    public final static String ITEMS_DATA = "item_data";

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.e("Broadcast: onReceive:");

        if(intent.getAction() != null) {
            Timber.e("Broadcast: onReceive: INTENT not NULL");

            if (ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
                updateWidget(context);
            }
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
        for (int appWidgetId : appWidgetIds) {
            ExpiringItemsWidgetService.startUpdateWidgetService(context, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, ArrayList<ItemPile> items) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_expiring_items_provider);

        if(items != null) {
            Intent intent = new Intent(context, ExpiringItemsRemoteViewsService.class);
            Timber.e("Broadcast: updateAppWidget --> items: %s", items.size());

            String widgetTitle = context.getResources().getString(R.string.widget_title) +
                        "(" + items.size() + ")";


            views.setTextViewText(R.id.widget_title, widgetTitle);
            views.setRemoteAdapter(R.id.widget_list_view, intent);
            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);
        } else {
            views.setViewVisibility(R.id.widget_list_view, View.GONE);
            views.setViewVisibility(R.id.widget_title, View.GONE);
            views.setViewVisibility(R.id.widget_error_layout, View.VISIBLE);

            Intent intent = new Intent(context, ExpiringItemsWidgetService.class);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pendingIntent = PendingIntent.getForegroundService(context, 1, intent, 0);
            } else {
                pendingIntent = PendingIntent.getService(context, 1, intent, 0);
            }
            views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingIntent);

            Intent intentMainActivity = new Intent(context, MainActivity.class);
            PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 1, intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_error_layout, activityPendingIntent);
        }

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
}