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
    private List<String> categoryNameList;

    private MutableLiveData<String> itemNameLiveData = new MutableLiveData<>();
    private MediatorLiveData<String> itemNameErrorText = new MediatorLiveData<>();
    private boolean isItemNameError = true;

    private MutableLiveData<String> itemExpiryDate = new MutableLiveData<>();
    private MediatorLiveData<String> itemExpiryErrorText = new MediatorLiveData<>();

    private MutableLiveData<String> quantityOfItems = new MutableLiveData<>();
    private MediatorLiveData<String> itemQuantityErrorText = new MediatorLiveData<>();
    private boolean isItemQuantityError = true;

    private MutableLiveData<String> caloriesPerItem = new MutableLiveData<>();
    private MediatorLiveData<String> itemCaloriesErrorText = new MediatorLiveData<>();
    private boolean isCaloriesPerItemError = true;

    private MutableLiveData<String> totalCalories = new MutableLiveData<>();
    private MutableLiveData<String> itemImageUri = new MutableLiveData<>();
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

        initItemNameValidation();
        initItemQuantityValidation();
        initItemCaloriesValidation();

        itemExpiryErrorText.addSource(itemExpiryDate, string -> {
            if(string != null && string.length() > 1) {
                itemExpiryErrorText.setValue(null);
            } else {
                itemExpiryErrorText.setValue("Required");
            }
        });

    }

    private void initItemNameValidation() {
        itemNameErrorText.addSource(itemNameLiveData, string -> {

            String errorText = validateInput("[\\p{P}\\p{S}]", string, 5,
                    "No special characters");
            if(errorText != null) {
                itemNameErrorText.setValue(errorText);
                isItemNameError = true;
            } else {
                if(isItemNameError) {
                    // Boolean check prevents null value for being set more than once -
                    // triggering a bug in the material design component. (see class docs above)
                    itemNameErrorText.setValue(null);
                }
                isItemNameError = false;
            }
        });
    }

    private void initItemQuantityValidation() {
        itemQuantityErrorText.addSource(quantityOfItems, string -> {
            String errorText = validateInput("[^0-9]", string, 1,
                    "0-9 only");
            if(errorText != null) {
                itemQuantityErrorText.setValue(errorText);
                isItemQuantityError = true;
            } else {
                if(isItemQuantityError) {
                    itemQuantityErrorText.setValue(null);
                }
                isItemQuantityError = false;
                updateTotalCalories();
            }
        });
    }

    private void initItemCaloriesValidation() {
        itemCaloriesErrorText.addSource(caloriesPerItem, string -> {
            String errorText = validateInput("[^0-9]", string, 1,
                    "0-9 only");
            if(errorText != null) {
                itemCaloriesErrorText.setValue(errorText);
                isCaloriesPerItemError = true;
            } else {
                if (isCaloriesPerItemError) {
                    itemCaloriesErrorText.setValue(null);
                }
                isCaloriesPerItemError = false;
                updateTotalCalories();
            }
        });
    }

    private String validateInput(String regex, String input,
                                  int minLength, String errorInvalidCharacters) {
        if(hasInvalidCharacters(regex, input)) {
            return errorInvalidCharacters;
        }

        if(minLength != 0) {
            if(minLength == 1 && input.length() < 1) {
                //Todo: fix string literal
                return "Required";
            } else if (input.length() < minLength) {
                return "Minimum length: " + minLength;
            }
        }
        return null;
    }

    private boolean hasInvalidCharacters(String regex, String string) {
        String filteredString = string.replaceAll(regex,"");
        return !string.equals(filteredString);
    }


    private void updateTotalCalories() {
        Log.e(LOG_TAG, "Quantity Error: " + isItemQuantityError);
        Log.e(LOG_TAG, "Calories Error: " + isCaloriesPerItemError);
        Log.e(LOG_TAG, "Quantity: " + quantityOfItems.getValue());
        Log.e(LOG_TAG, "Calories: " + caloriesPerItem.getValue());
        if(!isItemQuantityError && !isCaloriesPerItemError &&
            quantityOfItems.getValue() != null && caloriesPerItem.getValue() != null) {
            Integer total = Integer.parseInt(quantityOfItems.getValue()) *
                            Integer.parseInt(caloriesPerItem.getValue());
            totalCalories.setValue(total.toString());
        }
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

    public MutableLiveData<String> getItemExpiryDate() {
        return itemExpiryDate;
    }

    public LiveData<String> getItemExpiryErrorText() {
        return itemExpiryErrorText;
    }

    public MutableLiveData<String> getQuantityOfItems() {
        return quantityOfItems;
    }

    public LiveData<String> getItemQuantityErrorText() {
        return itemQuantityErrorText;
    }

    public MutableLiveData<String> getCaloriesPerItem() {
        return caloriesPerItem;
    }


    public LiveData<String> getItemCaloriesErrorText() {
        return itemCaloriesErrorText;
    }

    public LiveData<String> getTotalCaloriesLiveData() {
        return totalCalories;
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


    public void onAddItemClicked() {

        if(areAllInputsValid()) {

        };

//        ArrayList<Date> expiryList = new ArrayList<>();
//        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));
//        String catName = categoryNameLiveData.getValue();
//        ItemPile newItem = new ItemPile();
//        newItem.setItemName(itemNameLiveData.getValue());
//        newItem.setCategoryName(catName);
//        newItem.setItemCount(0);
//        newItem.setCalories(0);
//        newItem.setCounterType(CounterType.MILLILITRES);
//        newItem.setQuantity(0);
//        newItem.setExpiry(expiryList);
//
//
//        if(!getUserID().isEmpty()) {
//            itemRepository.addItem(getUserID(), getItemImageUri().getValue(), newItem,
//                    addItemStatus -> {
//                        switch(addItemStatus) {
//                            case (ADDING_ITEM):
//                                break;
//                            case (SUCCESS):
//                                isAddItemSuccessfulEvent.postValue(true);
//                                break;
//                            case(IMAGE_FAILED):
//                                Log.e(LOG_TAG, "Image Failed");
//                            case(FAILED):
//                                Log.e(LOG_TAG, "Item Failed");
//                                isAddItemSuccessfulEvent.postValue(false);
//                                break;
//                        }
//                    });
//        }
    }

    private boolean areAllInputsValid() {
        boolean isAllInputsValid = true;

        if(isItemNameError) {
            itemNameErrorText.setValue("Error.");
            isAllInputsValid = false;
        }
        if(isItemQuantityError) {
            itemQuantityErrorText.setValue("Error.");
            isAllInputsValid = false;
        }
        if(isCaloriesPerItemError) {
            itemCaloriesErrorText.setValue("Error");
            isAllInputsValid = false;
        }

        if(itemExpiryDate.getValue() == null || itemExpiryDate.getValue().length() < 1) {
            itemExpiryErrorText.setValue("Required");
            isAllInputsValid = false;
        }
        return isAllInputsValid;
    }
}
