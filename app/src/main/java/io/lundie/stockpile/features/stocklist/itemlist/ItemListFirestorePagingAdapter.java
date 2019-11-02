package io.lundie.stockpile.features.stocklist.itemlist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

import java.util.ArrayList;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.ItemPile;
import io.lundie.stockpile.utils.BindingViewHolder;

public class ItemListFirestorePagingAdapter extends FirestorePagingAdapter<ItemPile, BindingViewHolder> {

    private static final String LOG_TAG = ItemListFirestorePagingAdapter.class.getSimpleName();

    private ArrayList<ItemPile> itemPiles;

    public ItemListFirestorePagingAdapter(@NonNull FirestorePagingOptions<ItemPile> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BindingViewHolder holder, int position, @NonNull ItemPile itemPile) {
        //Object obj = getObjForPosition(position);
        holder.bind(itemPile);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.fragment_item_list_item;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(
                layoutInflater, viewType, parent, false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding);
        viewHolder.bind(this);
        return new BindingViewHolder(binding);
    }

//    @Override
//    public int getItemCount() {
//        if(itemPiles != null) {
//            Log.i(LOG_TAG, "Returning " + itemPiles.size());
//            return itemPiles.size();
//        } else {
//            Log.i(LOG_TAG, "Returning 0");
//            return 0;
//        }
//    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        //TODO: Handle error on front-end correctly
        if (state == LoadingState.ERROR) {
            Log.e(LOG_TAG, "Error during loading");
        }
    }

//    private Object getObjForPosition(int position) {
//        return itemPiles.get(position);
//    }

    public void onItemClicked(String itemName) {
        Log.e(LOG_TAG, "Registering item clicked:" + itemName);

    }

}
