package io.lundie.stockpile.features.stocklist.categorylist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.databinding.FragmentCategoryListItemBinding;

public class CategoriesViewAdapter2 extends RecyclerView.Adapter<CategoriesViewAdapter2.ViewHolder> {

    private static final String LOG_TAG = CategoriesViewAdapter2.class.getSimpleName();

    private ArrayList<ItemCategory> itemCategories;
    @NonNull
    @Override
    public CategoriesViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        FragmentCategoryListItemBinding binding = FragmentCategoryListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewAdapter2.ViewHolder holder, int position) {
        //holder.binding.setCategory(itemCategories.get(position));
    }

    @Override
    public int getItemCount() {
        if(itemCategories != null) {
            Log.i(LOG_TAG, "Returning Recycler Items: " + itemCategories.size());
            return  itemCategories.size();
        } else {
            Log.i(LOG_TAG, "Returning Recycler Items: " + 0);
            return 0;
        }
    }

    void setCategoryList(ArrayList<ItemCategory> itemCategories) {
        this.itemCategories = itemCategories;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FragmentCategoryListItemBinding binding;

        ViewHolder(FragmentCategoryListItemBinding binding) {

            super(binding.getRoot());

            this.binding = binding;

        }
    }
}
