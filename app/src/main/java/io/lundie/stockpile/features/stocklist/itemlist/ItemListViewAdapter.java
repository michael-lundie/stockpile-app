package io.lundie.stockpile.features.stocklist.itemlist;

import android.util.Log;

import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.utils.BindingBaseAdapter;

public class ItemListViewAdapter extends BindingBaseAdapter {

    private static final String LOG_TAG = ItemListViewAdapter.class.getSimpleName();

    private ArrayList<ItemPile> listTypeItems;
    private NavController navController;

    public ItemListViewAdapter(NavController navController) {
        super(navController);
        this.navController = navController;
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

    @Override
    public void onItemClicked(String itemName) {
        Log.e(LOG_TAG, "Registering item clicked:" + itemName);

    }
}
