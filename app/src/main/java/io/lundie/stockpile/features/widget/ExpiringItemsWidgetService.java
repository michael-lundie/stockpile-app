package io.lundie.stockpile.features.widget;

import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.android.DaggerIntentService;
import io.lundie.stockpile.MainActivity;
import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.features.authentication.UserManager;
import timber.log.Timber;

import static io.lundie.stockpile.features.widget.ExpiringItemsWidgetProvider.APP_WIDGET_EXPIRING_ID;

public class ExpiringItemsWidgetService extends DaggerIntentService {

    @Inject
    ItemListRepository itemListRepository;
    @Inject
    UserManager userManager;

    public static final int ONGOING_NOTIFICATION_ID = 50951;

    public ExpiringItemsWidgetService() {
        super("expiring_items_widget_service");
    }

    @Override
    public void onCreate() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, getResources().getString(R.string.channel_id))
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
        super.onCreate();
    }

    public static void startUpdateWidgetService(Context context, int appWidgetId) {
        Intent intent = new Intent(context, ExpiringItemsWidgetService.class);
        intent.putExtra(APP_WIDGET_EXPIRING_ID, appWidgetId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int appWidgetId = intent.getIntExtra(APP_WIDGET_EXPIRING_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Timber.d("Broadcast: onHandleIntent, Widget Service, NULL");
            return;
        }
        Timber.d("Broadcast: onHandleIntent, Widget Service, %s", appWidgetId);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);

        String userID = userManager.getUserID();
        if(userID != null && !userID.isEmpty()) {
            Timber.d("Broadcast: onHandleIntent --> USER ID OK, Widget Service, %s", appWidgetId);
            itemListRepository.getExpiringItemsWidgetList(
                    userManager.getUserID(), new ItemListRepository.WidgetListener() {
                        @Override
                        public void onComplete(ArrayList<ItemPile> itemPiles) {
                            Timber.d("Broadcast: onHandleIntent --> onComplete, Widget Service, %s", appWidgetId);
                            ExpiringItemsWidgetProvider.updateAppWidget(getApplicationContext(),
                                    appWidgetManager, appWidgetId, itemPiles);

                        }

                        @Override
                        public void onFail() {
                            ExpiringItemsWidgetProvider.updateAppWidget(getApplicationContext(),
                                    appWidgetManager, appWidgetId, null);
                        }
                    });
        } else {
            Timber.d("Broadcast: onHandleIntent --> no user id, Widget Service, %s", appWidgetId);
            ExpiringItemsWidgetProvider.updateAppWidget(getApplicationContext(),
                    appWidgetManager, appWidgetId, null);
        }

    }
}
