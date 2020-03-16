package io.lundie.stockpile.features.stocklist.manageitem;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseAdapter;
import io.lundie.stockpile.data.model.internal.ExpiryPile;
import timber.log.Timber;

public class ManageItemDateListViewAdapter extends BindingBaseAdapter {

    private static final String LOG_TAG = ManageItemDateListViewAdapter.class.getSimpleName();

    private ArrayList<ExpiryPile> expiryPileItems;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int itemId);
    }

    ManageItemDateListViewAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return expiryPileItems.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.fragment_manage_item_date_list_item;
    }

    @Override
    public int getItemCount() {
        if(expiryPileItems != null) {
            return expiryPileItems.size();
        } else {
            return 0;
        }
    }

    void setExpiryItems(ArrayList<ExpiryPile> expiryPileItems) {
        this.expiryPileItems = expiryPileItems;
        notifyDataSetChanged();
    }

    public void onRemoveButtonClicked(int itemId) {
        listener.onItemClick(itemId);
        Timber.e("Registering add clicked:");
    }
}
