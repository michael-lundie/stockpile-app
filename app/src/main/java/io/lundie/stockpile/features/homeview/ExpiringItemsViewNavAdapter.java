package io.lundie.stockpile.features.homeview;

import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseNavAdapter;
import io.lundie.stockpile.adapters.BindingPagingViewAdapter;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.features.stocklist.categorylist.CategoryFragmentDirections;
import io.lundie.stockpile.features.stocklist.categorylist.CategoryFragmentDirections.RelayCategoryAction;
import timber.log.Timber;

public class ExpiringItemsViewNavAdapter extends BindingPagingViewAdapter {

    private ArrayList<ItemPile> expiringItemsList;
    private NavController navController;

    ExpiringItemsViewNavAdapter(NavController navController) {
        super(navController);
        this.navController = navController;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return expiringItemsList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position, int loadingLayout) {
        if(expiringItemsList.get(position) == null) {
            Timber.i("Paging --> position is null! <--");
        }
        return expiringItemsList.get(position) == null ?
                loadingLayout : R.layout.fragment_home_expiring_list_item;
    }

    @Override
    public int getItemCount() {
        if(expiringItemsList != null) {
            Timber.i("Returning Recycler Items: %s", expiringItemsList.size());
            return  expiringItemsList.size();
        } else {
            Timber.i("Returning Recycler Items: %s", 0);
            return 0;
        }
    }

    void setExpiringItemsList(ArrayList<ItemPile> expiringItemsList) {
        this.expiringItemsList = expiringItemsList;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(String itemName) {
        Timber.e("Registering item clicked:%s", itemName);
        RelayCategoryAction relayCategoryAction = CategoryFragmentDirections.relayCategoryAction();
        relayCategoryAction.setCategory(itemName);
        navController.navigate(relayCategoryAction);
    }
}
