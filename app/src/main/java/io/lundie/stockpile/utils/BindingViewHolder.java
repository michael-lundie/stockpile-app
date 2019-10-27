package io.lundie.stockpile.utils;

import android.widget.BaseAdapter;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;


public class BindingViewHolder extends RecyclerView.ViewHolder {
    private final ViewDataBinding binding;

    public BindingViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object obj) {
        binding.setVariable(BR.obj, obj);
        binding.executePendingBindings();
    }

    public void bind(BindingBaseAdapter adapter){
        binding.setVariable(BR.adapter, adapter);
        binding.executePendingBindings();
    }
}