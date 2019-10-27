package io.lundie.stockpile.features.stocklist.categorylist;

import android.util.Log;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.ItemCategory;
import io.lundie.stockpile.utils.BindingBaseAdapter;

public class CategoriesViewAdapter extends BindingBaseAdapter {

    private static final String LOG_TAG = CategoriesViewAdapter.class.getSimpleName();

    private ArrayList<ItemCategory> itemCategories;

    public CategoriesViewAdapter(ArrayList itemList, BindingBaseAdapter.OnItemClickListener listener) {
        super(itemList, listener);
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

//    public void onItemClicked(String categoryName) {
//        Log.e(LOG_TAG, "Registering item clicked:" + categoryName);
//    }
}
