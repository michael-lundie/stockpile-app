package io.lundie.stockpile.features.stocklist.categorylist.di;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.lundie.stockpile.databinding.FragmentCategoryListItemBinding;

public class CategoriesViewAdapter extends RecyclerView.Adapter<CategoriesViewAdapter.ViewHolder> {
    @NonNull
    @Override
    public CategoriesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentCategoryListItemBinding binding = FragmentCategoryListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(FragmentCategoryListItemBinding binding) {

            super(binding.getRoot());

        }
    }
}
