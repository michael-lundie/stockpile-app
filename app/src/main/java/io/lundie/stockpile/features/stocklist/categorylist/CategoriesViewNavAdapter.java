package io.lundie.stockpile.features.stocklist.categorylist;

import android.util.Log;

import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseNavAdapter;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.features.stocklist.categorylist.CategoryFragmentDirections.RelayCategoryAction;

public class CategoriesViewNavAdapter extends BindingBaseNavAdapter {

    private static final String LOG_TAG = CategoriesViewNavAdapter.class.getSimpleName();

    private ArrayList<ItemCategory> itemCategories;
    private NavController navController;

    CategoriesViewNavAdapter(NavController navController) {
        super(navController);
        this.navController = navController;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return itemCategories.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_category_list_item;
    }

    @Override
    public int getItemCount() {
        if(itemCategories != null) {
            Log.i(LOG_TAG, "Returning Recycler Items: " + itemCategories.size());
            return  itemCategories.size();
        } else {
            Log.i(LOG_TAG, "Returning Recycler Items: " + 0);
            return 0;
        }
    }

    void setCategoryList(ArrayList<ItemCategory> itemCategories) {
        this.itemCategories = itemCategories;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(String itemName) {
        Log.e(LOG_TAG, "Registering item clicked:" + itemName);
        RelayCategoryAction relayCategoryAction = CategoryFragmentDirections.relayCategoryAction();
        relayCategoryAction.setCategory(itemName);
        navController.navigate(relayCategoryAction);
    }
}
