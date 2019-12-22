package io.lundie.stockpile.features.stocklist.itemlist;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseListenerAdapter;
import io.lundie.stockpile.data.model.ItemPile;

public class ItemListViewAdapter extends BindingBaseListenerAdapter {

    private static final String LOG_TAG = ItemListViewAdapter.class.getSimpleName();

    private ArrayList<ItemPile> listTypeItems;

    ItemListViewAdapter(OnItemClickListener listener) {
        super(listener);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return listTypeItems.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_item_list_item;
    }

    @Override
    public int getItemCount() {
        if(listTypeItems != null) {
            return listTypeItems.size();
        } else {
            return 0;
        }
    }

    void setListTypeItems(ArrayList<ItemPile> listTypeItems) {
        this.listTypeItems = listTypeItems;
        notifyDataSetChanged();
    }
}