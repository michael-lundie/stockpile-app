package io.lundie.stockpile.adapters;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;

import static io.lundie.stockpile.adapters.BindingBaseListenerAdapter.OnItemClickListener;

/**
 * A 'not-quite' generic view holder used to allow the use of bound objects
 * with a recycler view.
 */
class BindingViewHolder extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;

    BindingViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(Object obj) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }

    void bind(Object obj, OnItemClickListener listener) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
        binding.getRoot().setOnClickListener(view1 -> listener.onItemClick(obj));
    }

    void bind(RecyclerView.Adapter adapter){
        binding.setVariable(BR.adapter, adapter);
        binding.executePendingBindings();
    }
}