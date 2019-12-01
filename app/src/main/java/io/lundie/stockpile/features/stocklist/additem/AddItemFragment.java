package io.lundie.stockpile.features.stocklist.additem;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    private final int GALLERY_REQUEST_CODE = 31415;

    private AddItemViewModel addItemViewModel;

    private ImageView imageView;

    public AddItemFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        if (getArguments() != null) {
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
        imageView = binding.imageView;

        return binding.getRoot();
    }

    private void initViewModels() {
        addItemViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddItemViewModel.class);
    }

    public void onAddImageClicked() {
        Log.e(LOG_TAG, "OnAddImageClicked");
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            if(requestCode == GALLERY_REQUEST_CODE) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                addItemViewModel.setItemImageUri(selectedImage.toString());
                //imageView.setImageURI(selectedImage);
            }
    }

    public void onAddItemClicked() {
        addItemViewModel.getIsAddItemSuccessfulEvent().observe(this, isSuccessful -> {
            if(isSuccessful) {
                Log.e(LOG_TAG, "SUCCESS");
            } else {
                Log.e(LOG_TAG, "FAILURE");
            }
        });

        addItemViewModel.onAddItemClicked();
    }
}
