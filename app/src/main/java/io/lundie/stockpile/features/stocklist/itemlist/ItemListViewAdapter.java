package io.lundie.stockpile.features.stocklist.itemlist;

import android.util.Log;

import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.ItemPile;
import io.lundie.stockpile.utils.BindingBaseAdapter;

public class ItemListViewAdapter extends BindingBaseAdapter {

    private static final String LOG_TAG = ItemListViewAdapter.class.getSimpleName();

    private ArrayList<ItemPile> itemPiles;
    private NavController navController;

    public ItemListViewAdapter(NavController navController) {
        super(navController);
        this.navController = navController;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return itemPiles.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_item_list_item;
    }

    @Override
    public int getItemCount() {
        if(itemPiles != null) {
            return itemPiles.size();
        } else {
            return 0;
        }
    }

    void setItemPiles(ArrayList<ItemPile> itemPiles) {
        this.itemPiles = this.itemPiles;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(String itemName) {
        Log.e(LOG_TAG, "Registering item clicked:" + itemName);

    }
}
