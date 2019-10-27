package io.lundie.stockpile.features.stocklist.itemlist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.lundie.stockpile.utils.BindingBaseAdapter;
import io.lundie.stockpile.utils.BindingViewHolder;

public class ItemListViewAdapter extends BindingBaseAdapter {

    private static final String LOG_TAG = ItemListViewAdapter.class.getSimpleName();

    public ItemListViewAdapter(ArrayList itemList, OnItemClickListener listener) {
        super(itemList, listener);
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return null;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
