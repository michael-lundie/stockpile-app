package io.lundie.stockpile.features.stocklist.additem;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.lundie.stockpile.databinding.FragmentAddItemBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends DaggerFragment {

    private static final String LOG_TAG = AddItemFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private AddItemViewModel addItemViewModel;

    public AddItemFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        if(getArguments() != null) {
            String category = AddItemFragmentArgs.fromBundle(getArguments()).getCategory();
            addItemViewModel.setCategory(category);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddItemBinding binding = FragmentAddItemBinding.inflate(inflater, container, false);
        binding.setViewmodel(addItemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void initViewModels() {
        addItemViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddItemViewModel.class);
    }
}
