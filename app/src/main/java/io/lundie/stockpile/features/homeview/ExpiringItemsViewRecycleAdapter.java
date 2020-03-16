package io.lundie.stockpile.features.homeview;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.adapters.BindingBaseAdapter;
import io.lundie.stockpile.adapters.PagingAdapterListener;
import io.lundie.stockpile.data.model.firestore.ItemPile;
import timber.log.Timber;

public class ExpiringItemsViewRecycleAdapter extends BindingBaseAdapter {

    private MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    private ArrayList<ItemPile> expiringItemsList;
    private NavController navController;
    private PagingAdapterListener pagingListener;
    private View loadMoreButtonReference;

    ExpiringItemsViewRecycleAdapter(NavController navController) {
        this.navController = navController;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return expiringItemsList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        if(expiringItemsList.get(position) == null) {
            Timber.i("Paging --> position is null! <--");
            if(loadMoreButtonReference != null) {
                loadMoreButtonReference.setVisibility(View.VISIBLE);
            }
        }
        return expiringItemsList.get(position) == null ?
                R.layout.paged_item_loading : R.layout.fragment_expiring_items_list_item;
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

    void setPagingListener(PagingAdapterListener onEndListener) {
        this.pagingListener = onEndListener;
    }

    void setExpiringItemsList(ArrayList<ItemPile> expiringItemsList) {
        this.expiringItemsList = expiringItemsList;
        notifyDataSetChanged();
    }

    public void onItemClicked(Object object) {
        if(object instanceof ItemPile) {
            Timber.e("Registering item clicked:%s", ((ItemPile) object).getItemName());
            if(pagingListener != null) {
                pagingListener.onObjectClicked((ItemPile) object);
                navController.navigate(HomeFragmentDirections
                        .relayHomeExpiringToItemDest(((ItemPile) object).getItemName(), R.id.expiring_items_fragment));
            } else {
                Timber.e("Warning: No reference to paging listener.");
            }
        }
    }

    public void onLoadMoreClicked(View view) {
        loadMoreButtonReference = view;
        view.setVisibility(View.INVISIBLE);
        showLoading.setValue(true);
        pagingListener.onLoadMore();
    }

    public LiveData<Boolean> getShowLoading() {
        return showLoading;
    }
}
