package io.lundie.stockpile.features.targets;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.CategoryCheckListItem;
import io.lundie.stockpile.data.model.UserData;
import io.lundie.stockpile.data.repository.UserRepository;
import io.lundie.stockpile.features.FeaturesBaseViewModel;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

public class TargetsViewModel extends FeaturesBaseViewModel {

    private MediatorLiveData<ArrayList<CategoryCheckListItem>> categoryCheckList = new MediatorLiveData<>();

    private final UserRepository userRepository;
    private final AppExecutors appExecutors;

    @Inject
    TargetsViewModel(@NonNull Application application, UserRepository userRepository,
                     AppExecutors appExecutors) {
        super(application);
        this.userRepository = userRepository;
        this.appExecutors = appExecutors;
        preFetchCategoryList();
    }

    private void preFetchCategoryList() {
        if(userRepository.getUserDataSnapshot(getUserID()) != null) {
            UserData data = userRepository.getUserDataSnapshot(getUserID());
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

    LiveData<ArrayList<CategoryCheckListItem>> getCategoryCheckList() {
        return categoryCheckList;
    }

    public void onCheckClicked(String categoryName, Boolean isChecked) {
        Timber.e("Result from adapter > Cat Name: %s , Checked, %s", categoryName, isChecked);
    }
}
