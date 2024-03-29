package io.lundie.stockpile.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class has been modified from the code article below:
 * https://medium.com/androiddevelopers/android-data-binding-recyclerview-db7c40d9f0e4
 * Also see comment by Reza A. Ahmadi
 * https://medium.com/@alzahm/thank-you-for-your-great-post-i-learned-a-lot-e24de2371166
 */
public abstract class BindingBaseNavAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private NavController navController;

    public BindingBaseNavAdapter(NavController navController) {
        this.navController = navController;
    }

    @NonNull
    public BindingViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(
                layoutInflater, viewType, parent, false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding);
        viewHolder.bind(this);
        return new BindingViewHolder(binding);
    }

    public void onBindViewHolder(BindingViewHolder holder,
                                 int position) {
        Object obj = getObjForPosition(position);
        holder.bind(obj);
    }

    public void onItemClicked(String itemName) {}

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);
}
