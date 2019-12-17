package io.lundie.stockpile.features.stocklist.itemlist;

import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseNavAdapter;
import io.lundie.stockpile.data.model.ItemPile;

public class ItemListViewNavAdapter extends BindingBaseNavAdapter {

    private static final String LOG_TAG = ItemListViewNavAdapter.class.getSimpleName();

    private ArrayList<ItemPile> listTypeItems;
    private NavController navController;

    ItemListViewNavAdapter(NavController navController) {
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
        ItemListFragmentDirections.RelayItemListToItemAction relayItemListAction =
                ItemListFragmentDirections.relayItemListToItemAction();
        relayItemListAction.setItemName(itemName);
        navController.navigate(relayItemListAction);
    }
}
