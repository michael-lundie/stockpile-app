package io.lundie.stockpile.features.stocklist.item;

import java.util.ArrayList;
import java.util.Date;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseAdapter;
import timber.log.Timber;

public class ItemDateListViewAdapter extends BindingBaseAdapter {

    private ArrayList<Date> dates;

    ItemDateListViewAdapter() {}

    @Override
    protected Object getObjForPosition(int position) {
        return dates.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_item_date_list_item;
    }

    @Override
    public int getItemCount() {
        if(dates != null) {
            Timber.i("ADAPTER SIZE --> %s", dates.size());
            return dates.size();
        } else {
            Timber.i("ADAPTER SIZE --> 0");
            return 0;
        }
    }

    void setExpiryItems(ArrayList<Date> dates) {
        this.dates = dates;
        this.notifyDataSetChanged();
    }
}
