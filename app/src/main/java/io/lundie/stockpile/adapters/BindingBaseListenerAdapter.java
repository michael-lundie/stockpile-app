package io.lundie.stockpile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This is an extendable adapter for use with recycle views. It extends {@link BindingViewHolder},
 * allowing android data-binding to be used in recycle view holder XML layouts.
 *
 * This class has been modified from the code article below:
 * https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4
 * Also see comment by Reza A. Ahmadi
 * https://medium.com/@alzahm/thank-you-for-your-great-post-i-learned-a-lot-e24de2371166
 */
public abstract class BindingBaseListenerAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View view, Object obj);
    }

    public BindingBaseListenerAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(
                layoutInflater, viewType, parent, false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding);
        viewHolder.bind(this);
        return viewHolder;
    }

    public void onBindViewHolder(BindingViewHolder holder, int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj, listener);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public void onItemClicked(View view, Object obj) {
        listener.onItemClick(view, obj);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);
}
