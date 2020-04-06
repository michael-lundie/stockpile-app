package io.lundie.stockpile.features.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import io.lundie.stockpile.utils.DataUtils;
import timber.log.Timber;

public class ExpiringItemsWidgetListProvider implements RemoteViewsFactory {

    private ArrayList<ItemPile> mDataList;
    private Context context;
    private Intent intent;
    private DataUtils dataUtils;
    private int appWidgetId;

    ExpiringItemsWidgetListProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    private void initIntentDataRetrieval() {
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mDataList = intent.getParcelableArrayListExtra(ExpiringItemsWidgetService.ITEM_LIST);
        if(mDataList != null) {
            Timber.e("Broadcast: Retrieving dataList: %s", mDataList.size());
        }else {
            Timber.e("Broadcast: Retrieving dataList: NULL");
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
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString("widget_item_pile", null);
        if(json != null) {
            mDataList = getDataUtils().deserializeToItemPileArray(json);
        }
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
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_expiring_list_item);

        String itemName = mDataList.get(position).getItemName();
        remoteView.setTextViewText(R.id.widget_row, itemName);

        return remoteView;
    }

    private DataUtils getDataUtils() {
        if(dataUtils == null) {
            dataUtils = new DataUtils();
        }
        return dataUtils;
    }
}
