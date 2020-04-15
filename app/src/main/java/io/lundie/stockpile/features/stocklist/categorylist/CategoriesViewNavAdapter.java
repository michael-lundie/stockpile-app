package io.lundie.stockpile.features.stocklist.categorylist;

import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseNavAdapter;
import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.features.stocklist.categorylist.CategoryFragmentDirections.RelayCategoryAction;

public class CategoriesViewNavAdapter extends BindingBaseNavAdapter {

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
            return  itemCategories.size();
        } else {
            return 0;
        }
    }

    void setCategoryList(ArrayList<ItemCategory> itemCategories) {
        this.itemCategories = itemCategories;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(String itemName) {
        RelayCategoryAction relayCategoryAction = CategoryFragmentDirections.relayCategoryAction();
        relayCategoryAction.setCategory(itemName);
        navController.navigate(relayCategoryAction);
    }
}