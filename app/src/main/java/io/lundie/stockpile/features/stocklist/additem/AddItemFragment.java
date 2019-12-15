package io.lundie.stockpile.features.stocklist.additem;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;

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
    private int mEventId;

    public AddItemFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        if (getArguments() != null) {
            category = AddItemFragmentArgs.fromBundle(getArguments()).getCategory();
            addItemViewModel.setCategoryNameLiveData(category);
            mEventId = AddItemFragmentArgs.fromBundle(getArguments()).getAppEventId();
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            if(requestCode == GALLERY_REQUEST_CODE) {

                Uri selectedImage = data.getData();
                addItemViewModel.setItemImageUri(selectedImage.toString());
            }
    }

    public void onAddItemClicked() {
        addItemViewModel.getIsAddItemSuccessfulEvent().observe(this, statusEvent -> {
            switch (statusEvent.getErrorStatus()) {
                case (SUCCESS):
                case(SUCCESS_NO_IMAGE):

                    // This is navigation equivalent to popping the back-stack, preventing us
                    // from being able to navigate back to the add item form.
                    NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.item_list_fragment_dest, true).build();

                    addItemViewModel.getMessageController().setEventMessage(statusEvent.getEventText());

                    getNavController().navigate(
                            AddItemFragmentDirections
                                    .relayAddItemToItemListAction()
                                    .setCategory(category),
                            navOptions);
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