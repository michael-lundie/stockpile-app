package io.lundie.stockpile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import io.lundie.stockpile.R;
import timber.log.Timber;

/**
 * This class has been modified from the code article below:
 * https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4
 * Also see comment by Reza A. Ahmadi
 * https://medium.com/@alzahm/thank-you-for-your-great-post-i-learned-a-lot-e24de2371166
 */
public abstract class BindingPagingViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NavController navController;

    public BindingPagingViewAdapter(NavController navController) {
        this.navController = navController;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(
                layoutInflater, viewType, parent, false);
        if(viewType != R.layout.paged_item_loading) {
            BindingViewHolder viewHolder = new BindingViewHolder(binding);
            viewHolder.bind(this);
            return new BindingViewHolder(binding);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paged_item_loading, parent, false);
            Timber.i("Paging --> Returning Loading View Holder! <--");
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BindingViewHolder) {
            loadBindingViewHolder((BindingViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }

    }
    private void loadBindingViewHolder(BindingViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj);
    }

    private void showLoadingView(LoadingViewHolder holder, int position) {

    }


    public void onItemClicked(String itemName) {}

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position, R.layout.paged_item_loading);
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position, int loadingLayout);

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}