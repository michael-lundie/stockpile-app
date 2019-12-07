package io.lundie.stockpile.features.stocklist.additem;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemPile;
import io.lundie.stockpile.data.repository.ItemRepository;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.SingleLiveEvent;
import io.lundie.stockpile.utils.data.CounterType;
import io.lundie.stockpile.utils.data.FakeDataUtilHelper;

import static io.lundie.stockpile.features.stocklist.additem.AddItemStatusType.*;

/**
 * AddItemViewModel is responsible for managing the state of the AddItem view
 * and validating user input.
 *
 * TODO: Bugs:
 * https://github.com/material-components/material-components-android/issues/525
 */
public class AddItemViewModel extends FeaturesBaseViewModel{

    private static final String LOG_TAG = AddItemViewModel.class.getSimpleName();

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private MutableLiveData<String> categoryNameLiveData = new MutableLiveData<>();

    private MutableLiveData<String> itemNameLiveData = new MutableLiveData<>();
    private MediatorLiveData<String> itemNameErrorText = new MediatorLiveData<>();
    private boolean isItemNameError = false;

    private List<String> categoryNameList;


    private MutableLiveData<String> testEditText = new MutableLiveData<>();
    private MutableLiveData<String> itemImageUri = new MutableLiveData<>();

    private MediatorLiveData<String> confirmationText = new MediatorLiveData<>();

    private SingleLiveEvent<Boolean> isAddItemSuccessfulEvent = new SingleLiveEvent<>();

    @Inject
    AddItemViewModel(@NonNull Application application, ItemRepository itemRepository,
                     UserRepository userRepository) {
        super(application);
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;

        ArrayList<ItemCategory> itemCategories = userRepository.getCategoryData().getValue();
        if(itemCategories != null) {
            setCategoryNameList(itemCategories);
        }

        initItemNameFilter();
        confirmationText.addSource(testEditText, string -> {
            if(string.length() < 5 ) {
                confirmationText.postValue("Not okay");
            } else {
                confirmationText.postValue("okay!");
            }
        });
    }

    private void initItemNameFilter() {
        itemNameErrorText.addSource(itemNameLiveData, string -> {
            if(string.length() < 5 ) {
                itemNameErrorText.setValue("Not okay");
                isItemNameError = true;
            } else {

                Log.e(LOG_TAG, "Current string length: " + string.length());

                String filteredString = string.replaceAll("[\\p{P}\\p{S}]", "");

                Log.e(LOG_TAG, "Filtered length: " + filteredString.length());

                if (!string.equals(filteredString)) {
                    Log.e(LOG_TAG, "Not equal");
                    itemNameErrorText.setValue("Please do not use:");
                    isItemNameError = true;
                } else {
                    if(isItemNameError) {
                        // Boolean check prevents null value for being set more than once -
                        // triggering a bug in the material design component. (see class docs above)
                        itemNameErrorText.setValue(null);
                    } isItemNameError = false;
                }
            }
        });
    }


    public LiveData<String> getTestData() {
        return itemRepository.getTestLiveData();
    }

    @Override
    public void onAttemptingSignIn() {

    }

    @Override
    public void onSignInSuccess(String userID) {

    }

    @Override
    public void onSignedInAnonymously(String userID) {

    }

    @Override
    public void onSignInFailed() {

    }

    public MutableLiveData<String> getItemNameLiveData() {
        return itemNameLiveData;
    }

    public void setItemNameLiveData(MutableLiveData<String> itemNameText) {
        this.itemNameLiveData = itemNameText;
    }

    public LiveData<String> getItemNameErrorText() {
        return itemNameErrorText;
    }


    public MutableLiveData<String> getCategoryNameLiveData() {
        return categoryNameLiveData;
    }

    public void setCategoryNameLiveData(String string) {
        categoryNameLiveData.setValue(string);
    }

    public void setCategoryLiveData(MutableLiveData<String> categoryLiveData) {
        this.categoryNameLiveData = categoryLiveData;
    }

    public List<String> getCategoryNameList() {
        return categoryNameList;
    }

    private void setCategoryNameList(ArrayList<ItemCategory> itemCategories) {
        ArrayList<String> list = new ArrayList<>();
        for (ItemCategory category : itemCategories
             ) {
            list.add(category.getCategoryName());
        }
        this.categoryNameList = list;
    }


    public SingleLiveEvent<Boolean> getIsAddItemSuccessfulEvent() {
        return isAddItemSuccessfulEvent;
    }


    public void setItemImageUri(String uri) {
        itemImageUri.postValue(uri);
    }

    public LiveData<String> getItemImageUri() {
        return itemImageUri;
    }

    public MutableLiveData<String> getTestEditText() {
        return testEditText;
    }

    public void setTestEditText(MutableLiveData<String> testEditText) {
        this.testEditText = testEditText;
    }

    public MediatorLiveData<String> getConfirmationText() {
        return confirmationText;
    }

    public void setConfirmationText(MediatorLiveData<String> confirmationText) {
        this.confirmationText = confirmationText;
    }

    public void onAddItemClicked() {

        ArrayList<Date> expiryList = new ArrayList<>();
        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));

        String catName = categoryNameLiveData.getValue();
        ItemPile newItem = new ItemPile();
        newItem.setItemName(testEditText.getValue());
        newItem.setCategoryName(catName);
        newItem.setItemCount(0);
        newItem.setCalories(0);
        newItem.setCounterType(CounterType.MILLILITRES);
        newItem.setQuantity(0);
        newItem.setExpiry(expiryList);

        if(!getUserID().isEmpty()) {
            itemRepository.addItem(getUserID(), getItemImageUri().getValue(), newItem,
                    addItemStatus -> {
                        switch(addItemStatus) {
                            case (ADDING_ITEM):
                                break;
                            case (SUCCESS):
                                isAddItemSuccessfulEvent.postValue(true);
                                break;
                            case(IMAGE_FAILED):
                                Log.e(LOG_TAG, "Image Failed");
                            case(FAILED):
                                Log.e(LOG_TAG, "Item Failed");
                                isAddItemSuccessfulEvent.postValue(false);
                                break;
                        }
                    });
        }
    }
}
