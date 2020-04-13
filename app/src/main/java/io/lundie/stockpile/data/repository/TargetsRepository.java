package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.data.model.firestore.Target;
import io.lundie.stockpile.features.targets.TargetsTrackerType;
import io.lundie.stockpile.utils.threadpool.AppExecutors;
import timber.log.Timber;

/**
 * Repository class allowing CRUD access of {@link Target} objects in the firestore database.
 */
public class TargetsRepository extends BaseRepository {
    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;
    private FirestoreQueryLiveData targetsLiveData;

    @Inject
    TargetsRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    /**
     * Creates a {@link FirestoreQueryLiveData} on initialisation and returns LiveData for the
     * retrieved query snapshot.
     * @param userID ID of currently logged in User
     * @return {@link LiveData<QuerySnapshot>}
     */
    public LiveData<QuerySnapshot> getTargets(@NonNull String userID) {
        if (targetsLiveData == null || targetsLiveData.getValue() == null) {
            Query targetsQuery = collectionPath(userID).limit(10);
            targetsLiveData = new FirestoreQueryLiveData(targetsQuery);
        }
        return targetsLiveData;
    }

    /**
     * Adds a new {@link Target} to firestore database.
     * @param userID ID of currently logged in User
     * @param newTarget {@link Target} data to be added to firestore
     */
    public void addTarget(String userID, Target newTarget) {
        setTargetData(userID, newTarget, false, null);
    }

    /**
     * Updates {@link Target} data in the firestore database.
     * @param userID ID of currently logged in User
     * @param updatedTarget updated {@link Target} data to be added to firestore
     * @param originalTitle original title of the target - required since title is used as our
     *                      document I.D. If title is change, we must replace the entire document.
     */
    public void updateTarget(String userID, Target updatedTarget, String originalTitle) {
        setTargetData(userID, updatedTarget, true, originalTitle);
    }

    private void setTargetData(String userID, Target targetData, Boolean isUpdate,
                               @Nullable String originalTitle) {
        collectionPath(userID).document(targetData.getTargetName()).set(targetData);
        if(isUpdate && !targetData.getTargetName().equals(originalTitle)) {
            deleteTarget(userID, originalTitle);
        }
    }

    /**
     * Deletes a {@link Target} from the firestore database.
     * @param userID
     * @param targetName
     */
    public void deleteTarget(String userID, String targetName) {
        collectionPath(userID).document(targetName).delete();
    }

    /**
     * When new items are added to an {@link io.lundie.stockpile.data.model.firestore.ItemPile},
     * target data can be updated through this method.
     * @param userID ID of logged in user.
     * @param categoryName category of the new item/s that were uploaded
     * @param itemsAdded quantity of items added
     * @param caloriesAdded total calories of items added
     */
    public void updateTargetProgress(String userID, String categoryName, int itemsAdded,
                                     int caloriesAdded) {
         if(itemsAdded > 0 || caloriesAdded > 0) {
             collectionPath(userID)
                     .whereArrayContains("trackedCategories", categoryName)
                     .get().addOnCompleteListener(appExecutors.diskIO(), task -> {
                 if(task.isSuccessful() && task.getResult() != null) {
                     List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                     for(DocumentSnapshot snapshot : snapshots) {
                         Target target = snapshot.toObject(Target.class);
                         processProgressUpdate(userID, categoryName, target, itemsAdded, caloriesAdded);
                     }
                 } else {
                     Timber.e(task.getException(), "Error getting tacked categories for update");
                 }
             });
         }
    }

    private void processProgressUpdate(String userID, String categoryName, Target target,
                                       int itemsAdded, int caloriesAdded) {
        if(target != null) {
            for(String trackedCategory : target.getTrackedCategories()) {
                if(trackedCategory.equals(categoryName)) {
                    if(target.getTargetType() == TargetsTrackerType.ITEMS) {
                        if(itemsAdded > 0) {
                            target.setTargetProgress((target.getTargetProgress() + itemsAdded));
                        }
                    } else {
                        if(caloriesAdded > 0) {
                            target.setTargetProgress((target.getTargetProgress() + caloriesAdded));
                        }
                    }
                    setTargetData(userID, target, false, null);
                }
            }
        }
    }

    @Override
    CollectionReference collectionPath(@NonNull String userID) {
        return firestore.collection("users")
                .document(userID)
                .collection("targets");
    }
}