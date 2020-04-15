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

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import timber.log.Timber;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Implementation of App Widget functionality.
 */
public class ExpiringItemsWidgetProvider extends AppWidgetProvider {

    public final static String APP_WIDGET_EXPIRING_ID = "app_widget_expiring_id";
    public final static String DISABLE_FOR_SIGNOUT = "disable_for_signout";

    private static boolean isDisabled = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null) {
            if (ACTION_APPWIDGET_UPDATE.equals(intent.getAction())){
                isDisabled = intent.getBooleanExtra(DISABLE_FOR_SIGNOUT, true);
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

        RemoteViews views = createNewRemoteView(context);

        if(items != null && !isDisabled) {
            Intent intent = new Intent(context, ExpiringItemsRemoteViewsService.class);
            String widgetTitle = context.getResources().getString(R.string.widget_title) + "(" + items.size() + ")";
            views.setViewVisibility(R.id.widget_error_layout, View.GONE);
            views.setViewVisibility(R.id.widget_title, View.VISIBLE);
            views.setTextViewText(R.id.widget_title, widgetTitle);
            views.setRemoteAdapter(R.id.widget_list_view, intent);
            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);
        } else {
            if(isDisabled) {
                showErrorLayout(views, context.getResources().getString(R.string.widget_signed_out));
            } else {
                showErrorLayout(views, null);
            }

            Intent intent = new Intent(context, ExpiringItemsWidgetService.class);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pendingIntent = PendingIntent.getForegroundService(context, 1, intent, 0);
            } else {
                pendingIntent = PendingIntent.getService(context, 1, intent, 0);
            }
            views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingIntent);
        }

        // Update Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onEnabled(Context context) {
        showErrorLayout(createNewRemoteView(context), context.getResources().getString(R.string.widget_signed_out));
    }

    @Override
    public void onDisabled(Context context) {
        showErrorLayout(createNewRemoteView(context), "Signed Out");
        // Enter relevant functionality for when the last widget is disabled
    }

    private static RemoteViews createNewRemoteView(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.widget_expiring_items_provider);
    }

    private static void showErrorLayout(RemoteViews views, String customText) {
        views.setViewVisibility(R.id.widget_list_view, View.GONE);
        views.setViewVisibility(R.id.widget_title, View.GONE);
        views.setViewVisibility(R.id.widget_error_layout, View.VISIBLE);
        if(customText != null && !customText.isEmpty()) {
            views.setTextViewText(R.id.widget_error_text, customText);
        }
    }
}