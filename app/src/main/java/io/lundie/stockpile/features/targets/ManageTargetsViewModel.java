package io.lundie.stockpile.features.targets;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.CategoryCheckListItem;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.features.targets.FrequencyTrackerType.FrequencyTrackerTypeDef;
import io.lundie.stockpile.features.targets.TargetsTrackerType.TargetsTrackerTypeDef;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

public class ManageTargetsViewModel extends FeaturesBaseViewModel {

    private final UserRepository userRepository;
    private final AppExecutors appExecutors;

    private MediatorLiveData<ArrayList<CategoryCheckListItem>> categoryCheckList = new MediatorLiveData<>();
    private MutableLiveData<TargetsTrackerTypeWrapper> trackerTarget;
    private MutableLiveData<FrequencyTrackerTypeWrapper> trackerFrequency;
    private MutableLiveData<String> addEditIconButtonText = new MutableLiveData<>();

    @Inject
    ManageTargetsViewModel(@NonNull Application application, UserRepository userRepository,
                           AppExecutors appExecutors) {
        super(application);
        this.userRepository = userRepository;
        this.appExecutors = appExecutors;
        preFetchCategoryList();
        setUpTrackerLiveData();
    }

    private void initEditMode() {
        addEditIconButtonText.setValue(getApplication().getResources().getString(R.string.edit_target));
    }

    private void setUpTrackerLiveData() {
        trackerTarget = new MutableLiveData<>(new TargetsTrackerTypeWrapper(TargetsTrackerType.NONE));
        trackerFrequency = new MutableLiveData<>(new FrequencyTrackerTypeWrapper(FrequencyTrackerType.NONE));
    }

    private void preFetchCategoryList() {
        if(userRepository.getStaticUserDataSnapshot(getUserID()) != null) {
            UserData data = userRepository.getStaticUserDataSnapshot(getUserID());
            categoryCheckList.setValue(buildCategoryCheckList(data.getCategories()));
        } else if(userRepository.getUserDocSnapshotLiveData() != null) {
            // Use the live data source to grab the category list. It will have already been built.
            // This prevents unnecessary requests to firestore.
            appExecutors.diskIO().execute(() -> categoryCheckList.addSource(
                    userRepository.getUserDocSnapshotLiveData(), snapshot -> {
                if(snapshot != null) {
                    UserData data = snapshot.toObject(UserData.class);
                    if(data != null) {
                        categoryCheckList.postValue(buildCategoryCheckList(data.getCategories()));
                        categoryCheckList.removeSource(userRepository.getUserDocSnapshotLiveData());
                    }
                }
            }));
        }
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

    public MutableLiveData<TargetsTrackerTypeWrapper> getTrackerTarget() {
        return trackerTarget;
    }

    public MutableLiveData<FrequencyTrackerTypeWrapper> getTrackerFrequency() {
        return trackerFrequency;
    }

    public void setTrackerTarget(MutableLiveData<TargetsTrackerTypeWrapper> trackerTarget) {
        this.trackerTarget = trackerTarget;
    }

    public void setTrackerFrequency(MutableLiveData<FrequencyTrackerTypeWrapper> trackerFrequency) {
        this.trackerFrequency = trackerFrequency;
    }

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

    public void onCaloriesSelected() {setTrackerTargets(TargetsTrackerType.CALORIES);}

    public void onItemsSelected() {setTrackerTargets(TargetsTrackerType.ITEMS);}

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
    
    void onAddTargetClicked() {
        if(areAllInputsValid()) {
            userRepository.get
        }
    }

    //TODO: Data Validation
    private boolean areAllInputsValid() {
        return true;
    }

}
