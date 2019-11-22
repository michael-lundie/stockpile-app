package io.lundie.stockpile.features.stocklist.item;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.databinding.FragmentItemBinding;

/**
 * A simple {@link DaggerFragment} subclass.
 */
public class ItemFragment extends DaggerFragment {

    private static final String LOG_TAG = ItemFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ItemViewModel itemViewModel;
    private String itemID;

    public ItemFragment() { /* Required empty constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemViewModel.class);
        if(getArguments() != null) {
            itemID = ItemFragmentArgs.fromBundle(getArguments()).getItemId();
        }
        Log.d(LOG_TAG, "ItemFragment: Requested Id is --> " + itemID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentItemBinding binding = FragmentItemBinding.inflate(inflater, container, false);
        binding.setViewmodel(itemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        requestItemObserver();
        return binding.getRoot();
    }

    private void requestItemObserver() {

    }


}
