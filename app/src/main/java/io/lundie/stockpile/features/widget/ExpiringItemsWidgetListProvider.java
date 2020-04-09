package io.lundie.stockpile.features.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.data.repository.ItemListRepository;
import io.lundie.stockpile.features.authentication.UserManager;
import io.lundie.stockpile.utils.DateUtils;
import timber.log.Timber;

public class ExpiringItemsWidgetListProvider implements RemoteViewsFactory {

    private final ItemListRepository itemListRepository;
    private final UserManager userManager;
    private ArrayList<ItemPile> mDataList;
    private Context context;
    private Intent intent;
    private int appWidgetId;

    ExpiringItemsWidgetListProvider(Context context, Intent intent,
                                    ItemListRepository itemListRepository, UserManager userManager) {
        this.context = context;
        this.intent = intent;
        this.itemListRepository = itemListRepository;
        this.userManager = userManager;
    }

    private void initIntentDataRetrieval() {
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        String userID = userManager.getUserID();
        if(userID != null && !userID.isEmpty()) {
            itemListRepository.getExpiringItemsWidgetList(userManager.getUserID(),
                    new ItemListRepository.WidgetListener() {
                        @Override
                        public void onComplete(ArrayList<ItemPile> itemPiles) {
                            mDataList = itemPiles;
                            if(mDataList != null) {
                                Timber.e("Broadcast: Retrieving dataList: %s", mDataList.size());
                            }else {
                                Timber.e("Broadcast: Retrieving dataList: NULL");
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    });
        }
    }

    @Override
    public void onCreate() {
        Timber.e("Broadcast (list provider): onCreate");
        initIntentDataRetrieval();
    }

    @Override
    public void onDataSetChanged() {
        Timber.e("Broadcast (list provider): onDataSetChanged");
        initIntentDataRetrieval();
    }

    @Override
    public void onDestroy() {
        Timber.e("Broadcast (list provider): onDestroy");

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() { return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getCount() {
        if(mDataList != null) {
            Timber.e("Returning %s", mDataList.size());
            return mDataList.size();
        } Timber.e("Returning 0");
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_expiring_list_item);

        String itemName = mDataList.get(position).getItemName();
        String expiring = DateUtils.dateToString(mDataList.get(position).getExpiry().get(0));
        remoteView.setTextViewText(R.id.widget_row_item_title, itemName);
        remoteView.setTextViewText(R.id.widget_row_item_date, expiring);
        return remoteView;
    }
}
