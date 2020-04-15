package io.lundie.stockpile.features.targets;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseListenerAdapter;
import io.lundie.stockpile.data.model.internal.CategoryCheckListItem;
import timber.log.Timber;

public class ManageTargetsRecycleAdapter extends BindingBaseListenerAdapter {

    private ArrayList<CategoryCheckListItem> categoryItems;

    ManageTargetsRecycleAdapter(OnItemClickListener listener) {
        super(listener);
    }

    @Override
    protected Object getObjForPosition(int position) {
        return categoryItems.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_targets_add_cat_item;
    }

    @Override
    public int getItemCount() {
        if(categoryItems != null) {
            return categoryItems.size();
        } else {
            return 0;
        }
    }

    void setCategoryItems(ArrayList<CategoryCheckListItem> categoryItems) {
        this.categoryItems = categoryItems;
    }
}