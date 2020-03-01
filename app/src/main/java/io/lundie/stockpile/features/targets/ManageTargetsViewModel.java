package io.lundie.stockpile.features.targets;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.CategoryCheckListItem;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.Target;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateEventWrapper;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.targets.FrequencyTrackerType.FrequencyTrackerTypeDef;
import io.lundie.stockpile.features.targets.TargetsTrackerType.TargetsTrackerTypeDef;
import io.lundie.stockpile.utils.AppExecutors;
import io.lundie.stockpile.utils.SingleLiveEvent;
import timber.log.Timber;

import static io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType.*;
import static io.lundie.stockpile.utils.ValidationUtils.*;
import static io.lundie.stockpile.utils.ValidationUtils.validateInput;
import static io.lundie.stockpile.utils.ValidationUtilsErrorType.*;

public class ManageTargetsViewModel extends FeaturesBaseViewModel{

    private final UserRepository userRepository;
    private final AppExecutors appExecutors;

    private MediatorLiveData<ArrayList<CategoryCheckListItem>> categoryCheckList = new MediatorLiveData<>();
    private MutableLiveData<Boolean> isCategorySelectionError = new MutableLiveData<>(false);

    private MediatorLiveData<String> targetName = new MediatorLiveData<>();
    private MediatorLiveData<String> targetNameErrorText = new MediatorLiveData<>();
    private int targetNameCharCount = 0;
    private boolean isTargetNameError = true;
    private MutableLiveData<TargetsTrackerTypeWrapper> trackerTarget;
    private MutableLiveData<Boolean> isTrackerTargetError = new MutableLiveData<>(false);
    private MutableLiveData<FrequencyTrackerTypeWrapper> trackerFrequency;
    private MutableLiveData<Boolean> isTrackerFrequencyError = new MutableLiveData<>(false);

    private MutableLiveData<String> targetQuantity = new MutableLiveData<>();
    private MediatorLiveData<String> targetQuantityErrorText = new MediatorLiveData<>();
    private boolean isTargetQuantityError = true;

    private MutableLiveData<String> addEditIconButtonText = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAttemptingUpload = new MutableLiveData<>(false);
    private boolean isNameMediatorDataStopped = false;
    private SingleLiveEvent<UserDataUpdateEventWrapper> isUpdateSuccessfulEvent = new SingleLiveEvent<>();

    @Inject
    ManageTargetsViewModel(@NonNull Application application, UserRepository userRepository,
                           AppExecutors appExecutors) {
        super(application);
        this.userRepository = userRepository;
        this.appExecutors = appExecutors;
        initDataFetchFromRepo();
        initTargetNameMediatorData();
        initLiveValidation();
    }

    private void initLiveValidation() {
        targetNameErrorText.addSource(targetName, string -> {
            Timber.e("--> Detecting source change");
            if (!isNameMediatorDataStopped && string != null
                    &&(string.length() == targetNameCharCount + 1
                    || string.length() == targetNameCharCount - 1)) {
                Timber.e("--> Stopping target name mediator data.");
                stopTargetNameMediatorData();
            }
            validateTargetName(string);
        });

        targetQuantityErrorText.addSource(targetQuantity, this::validateTargetQuantity);
    }

    private void validateTargetQuantity(String string) {
        String errorText = retrieveValidationText(validateInput(numbersRegEx, string, 1),
                getApplication().getResources().getString(R.string.form_error_invalid_chars_number_only));

        if (!errorText.isEmpty()) {
            targetQuantityErrorText.setValue(errorText);
            isTargetQuantityError = true;
        } else {
            if (isTargetQuantityError) {
                // Boolean check prevents null value for being set more than once -
                // triggering a bug in the material design component. (see class docs above)
                targetQuantityErrorText.setValue(null);
            }
            isTargetQuantityError = false;
        }
    }

    private void validateTargetName(String string) {
        String errorText = retrieveValidationText(validateInput(specialCharsRegEx, string, 5),
                getApplication().getResources().getString(R.string.form_error_invalid_chars_no_special));

        if (!errorText.isEmpty()) {
            targetNameErrorText.setValue(errorText);
            isTargetNameError = true;
        } else {
            if (isTargetNameError) {
                // Boolean check prevents null value for being set more than once -
                // triggering a bug in the material design component. (see class docs above)
                targetNameErrorText.setValue(null);
            }
            isTargetNameError = false;
        }
    }

    private String retrieveValidationText(int errorType, String invalidCharactersErrorText) {
        Timber.e("Error type is equal to : %s", errorType);
        switch (errorType) {
            case NULL_INPUT: Timber.e("REGISTERING NULL");
            case EMPTY_FIELD: return getApplication().getResources().getString(R.string.form_error_empty_field);
            case INVALID_CHARS: return invalidCharactersErrorText;
            case MIN_NOT_REACHED: return getApplication().getResources().getString(R.string.form_error_min_not_reached);
            case VALID: return "";
        } return "";
    }

    private void initTargetNameMediatorData() {
        trackerTarget = new MutableLiveData<>(new TargetsTrackerTypeWrapper(TargetsTrackerType.NONE));
        targetName.addSource(trackerTarget, tracker -> updateTargetName());
        trackerFrequency = new MutableLiveData<>(new FrequencyTrackerTypeWrapper(FrequencyTrackerType.NONE));
        targetName.addSource(trackerFrequency, tracker -> updateTargetName());
    }

    private void stopTargetNameMediatorData() {
        Timber.e("--> Removing Target Mediator Data.");
        targetName.removeSource(trackerTarget);
        targetName.removeSource(trackerFrequency);
        isNameMediatorDataStopped = true;
    }

    private void updateTargetName() {
        String name = "";
        if(trackerFrequency != null && trackerFrequency.getValue() != null &&
                trackerFrequency.getValue().getTypeDef() != FrequencyTrackerType.NONE) {
            isTrackerFrequencyError.setValue(false);
            if(trackerFrequency.getValue().getTypeDef() == FrequencyTrackerType.WEEKLY) {
                name = getApplication().getResources().getString(R.string.weekly);
            } else if (trackerFrequency.getValue().getTypeDef() == FrequencyTrackerType.MONTHLY) {
                name = getApplication().getResources().getString(R.string.monthly);
            }
        }
        if(trackerTarget != null && trackerTarget.getValue() != null &&
                trackerTarget.getValue().getTypeDef() != TargetsTrackerType.NONE) {
            isTrackerTargetError.setValue(false);
            if(trackerTarget.getValue().getTypeDef() == TargetsTrackerType.ITEMS) {
                name = name + " " + getApplication().getResources().getString(R.string.items);
            } else if (trackerTarget.getValue().getTypeDef() == TargetsTrackerType.CALORIES) {
                name = name + " " + getApplication().getResources().getString(R.string.calories);
            }
        }
        name = name + " " + getApplication().getResources().getString(R.string.target);
        targetName.setValue(name);
        targetNameCharCount = name.length();
    }

    public MutableLiveData<String> getTargetName() {
        return targetName;
    }

    public LiveData<String> getTargetNameErrorText() {
        return targetNameErrorText;
    }

    public MutableLiveData<String> getTargetQuantity() {
        return targetQuantity;
    }

    public LiveData<String> getTargetQuantityErrorText() { return targetQuantityErrorText; }

    private void initEditMode() {
        addEditIconButtonText.setValue(getApplication().getResources().getString(R.string.edit_target));
    }

    private void initDataFetchFromRepo() {
        if(userRepository.getUserDocumentRealTimeData() == null) {
            userRepository.initUserDocumentRealTimeUpdates(getUserID());
        }
        UserData userData = userRepository.fetchMostRecentUserDocumentData(getUserID());
        appExecutors.diskIO().execute(() -> {
            categoryCheckList.postValue(buildCategoryCheckList(userData.getCategories()));
        });
    }

    private ArrayList<CategoryCheckListItem> buildCategoryCheckList(ArrayList<ItemCategory> itemCategories) {
        ArrayList<CategoryCheckListItem> categoryCheckListItems = new ArrayList<>();
        for (ItemCategory category : itemCategories) {
            CategoryCheckListItem checkListItem = new CategoryCheckListItem();
            checkListItem.setCategoryName(category.getCategoryName());
            checkListItem.setChecked(false);
            categoryCheckListItems.add(checkListItem);
        }
        return categoryCheckListItems;
    }

    public LiveData<ArrayList<CategoryCheckListItem>> getCategoryCheckList() {
        return categoryCheckList;
    }

    void onCheckClicked(String categoryName, Boolean isChecked) {
        if(categoryCheckList.getValue() != null) {
            ArrayList<CategoryCheckListItem> items = categoryCheckList.getValue();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getCategoryName().equals(categoryName)) {
                    CategoryCheckListItem item = items.get(i);
                    item.setChecked(isChecked);
                    items.set(i, item);
                    categoryCheckList.postValue(items);
                    Timber.e("Modified LiveData value for %s with : %s ", categoryName, categoryCheckList.getValue().get(i).getIsChecked());
                    break;
                }
            }
        }
    }

    public LiveData<Boolean> getIsCategorySelectionError() { return isCategorySelectionError; }

    public MutableLiveData<TargetsTrackerTypeWrapper> getTrackerTarget() {
        return trackerTarget;
    }

    public MutableLiveData<FrequencyTrackerTypeWrapper> getTrackerFrequency() {
        return trackerFrequency;
    }

    public LiveData<Boolean> getIsTrackerTargetError() { return isTrackerTargetError; }

    public LiveData<Boolean> getIsTrackerFrequencyError() { return isTrackerFrequencyError; }

    public void onWeekSelected() {
        setTrackerFrequency(FrequencyTrackerType.WEEKLY);
    }

    public void onMonthSelected() {
        setTrackerFrequency(FrequencyTrackerType.MONTHLY);
    }

    private void setTrackerFrequency(@FrequencyTrackerTypeDef int frequency) {
        FrequencyTrackerTypeWrapper wrapper = trackerFrequency.getValue();
        if(wrapper != null) {
            wrapper.setTypeDef(frequency);
            trackerFrequency.setValue(wrapper);
        }
    }

    public void setTargetType(@TargetsTrackerTypeDef int targetType) {
        setTrackerTargets(targetType);
    }

    private void setTrackerTargets(@TargetsTrackerTypeDef int target) {
        TargetsTrackerTypeWrapper wrapper = trackerTarget.getValue();
        if(wrapper != null) {
            wrapper.setTypeDef(target);
            trackerTarget.setValue(wrapper);
        }
    }

    public LiveData<Boolean> getIsAttemptingUpload() {
        return isAttemptingUpload;
    }

    public LiveData<String> getAddEditIconButtonText() {
        return addEditIconButtonText;
    }

    public SingleLiveEvent<UserDataUpdateEventWrapper> getIsUpdateSuccessfulEvent() {
        return isUpdateSuccessfulEvent;
    }

    void onAddTargetClicked() {
        if(areAllInputsValid()) {
            Timber.e("ALL INPUTS VALID");
            Target newTarget = new Target();
            // NOTE: there will be no nulls at this stage as validation has already been carried out.
            newTarget.setTargetType(trackerTarget.getValue().getTypeDef());
            newTarget.setTargetFrequency(trackerFrequency.getValue().getTypeDef());
            newTarget.setTargetGoal(Integer.parseInt(getTargetQuantity().getValue()));
            ArrayList<String> trackedCategories = new ArrayList<>();
            for (CategoryCheckListItem item : categoryCheckList.getValue()) {
                if(item.getIsChecked()) {
                    trackedCategories.add(item.getCategoryName());
                }
            }
            newTarget.setTrackedCategories(trackedCategories);
            newTarget.setTargetName(targetName.getValue());

            if (!getUserID().isEmpty()) {
                isAttemptingUpload.setValue(true);
                userRepository.addTarget(getUserID(), newTarget, this::handleUpdateStatusEvents);
            }

            //userRepository.addTarget(getUserID(), );

        } else {
            Timber.e("NOT VALID");
            //TODO: Post validation error using single live event
        }
    }

    private void handleUpdateStatusEvents(int updateStatus) {
        switch(updateStatus) {
            case UPDATING:
                break;
            case SUCCESS:
                isAttemptingUpload.setValue(false);
                postUpdateStatusEvent(updateStatus, "Success");
                break;
            case FAILED:
                isAttemptingUpload.setValue(false);
                postUpdateStatusEvent(updateStatus, "Failed");
                break;
        }
    }

    private void postUpdateStatusEvent(int updateStatus, String eventMessage) {
        UserDataUpdateEventWrapper updateWrapper = new UserDataUpdateEventWrapper();
        updateWrapper.setTypeDef(updateStatus);
        updateWrapper.setEventText(eventMessage);
        isUpdateSuccessfulEvent.postValue(updateWrapper);
    }

    //TODO: Data Validation
    private boolean areAllInputsValid() {
        boolean isInputValid = true;

        if(trackerTarget.getValue() == null || trackerTarget.getValue().getTypeDef() == 0) {
            isTrackerTargetError.setValue(true);
            Timber.e("Tracker Tracker not valid");
            isInputValid = false;
        }

        if(trackerFrequency.getValue() == null || trackerFrequency.getValue().getTypeDef() == 0) {
            isTrackerFrequencyError.setValue(true);
            Timber.e("Frequency Tracker not valid");
            isInputValid = false;
        }

        if(targetQuantity.getValue() == null || targetQuantity.getValue().isEmpty()) {
            Timber.e("Quantity not valid");
            validateTargetQuantity(targetQuantity.getValue());
            isInputValid = false;
        }

        if(isTargetNameError) {
            Timber.e("Target Name not valid");
            validateTargetName(targetName.getValue());
            isInputValid = false;
        }

        if(categoryCheckList.getValue() != null) {
            boolean hasCheckedItems = false;
            for(CategoryCheckListItem item : categoryCheckList.getValue()) {
                if(item.getIsChecked()) {
                    isCategorySelectionError.setValue(false);
                    hasCheckedItems = true;
                    break;
                }
            }
            if(!hasCheckedItems) {
                isCategorySelectionError.setValue(true);
                isInputValid = false;
            }
        } else {
            isInputValid = false;
        }

        return isInputValid;
    }
}
