package io.lundie.stockpile.features.stocklist.additem;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.Calendar;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.databinding.FragmentAddItemBinding;
import io.lundie.stockpile.features.FeaturesBaseFragment;

import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.FAILED;
import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.IMAGE_FAILED;
import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.SUCCESS;
import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.SUCCESS_NO_IMAGE;
import static io.lundie.stockpile.utils.AppUtils.calendarToLocalDate;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends FeaturesBaseFragment {

    private static final String LOG_TAG = AddItemFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private final int GALLERY_REQUEST_CODE = 31415;

    private AddItemViewModel addItemViewModel;

    private Calendar calendar;
    private TextInputEditText dateEditText;
    private String category;

    public AddItemFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        if (getArguments() != null) {
            category = AddItemFragmentArgs.fromBundle(getArguments()).getCategory();
            addItemViewModel.setCategoryNameLiveData(category);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setNavController(container);
        FragmentAddItemBinding binding = FragmentAddItemBinding.inflate(inflater, container, false);
        binding.setViewmodel(addItemViewModel);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setHandler(this);
        setUpDateDialog(binding);
        return binding.getRoot();
    }

    private void setUpDateDialog(FragmentAddItemBinding binding) {
        calendar = Calendar.getInstance();
        dateEditText = binding.expiryDateEditText;
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField(calendar);
                };
        dateEditText.setOnClickListener(view -> {
            new DatePickerDialog(getActivity(),
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
    }

    private void updateDateField(Calendar calendar) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
        LocalDate localDate = calendarToLocalDate(calendar);
        String formattedDate = localDate.format(dateFormatter);
        dateEditText.setText(formattedDate);
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
        addItemViewModel.getIsAddItemSuccessfulEvent().observe(this, statusEvent -> {
            switch (statusEvent.getErrorStatus()) {
                case (SUCCESS):
                    AddItemFragmentDirections.RelayAddItemToItemListAction toItemListAction =
                            AddItemFragmentDirections.relayAddItemToItemListAction();
                    toItemListAction.setCategory(category);
                    toItemListAction.setEventString(statusEvent.getEventText());
                    getNavController().navigate(toItemListAction);
                    break;
                case(SUCCESS_NO_IMAGE):
                    break;
                case(IMAGE_FAILED):
                    break;
                case(FAILED):
                    break;
            }
        });
        addItemViewModel.onAddItemClicked();
    }


}
