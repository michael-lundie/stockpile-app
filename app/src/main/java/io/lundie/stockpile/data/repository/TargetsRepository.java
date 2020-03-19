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
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

public class TargetsRepository extends BaseRepository {
    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;
    private FirestoreQueryLiveData targetsLiveData;

    @Inject
    TargetsRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public LiveData<QuerySnapshot> getTargets(@NonNull String userID) {
        if (targetsLiveData == null || targetsLiveData.getValue() == null) {
            Query targetsQuery = collectionPath(userID).limit(10);
            targetsLiveData = new FirestoreQueryLiveData(targetsQuery);
        }
        return targetsLiveData;
    }

    public void addTarget(String userID, Target newTarget) {
        setTargetData(userID, newTarget, false, null);
    }

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

    public void deleteTarget(String userID, String targetName) {
        collectionPath(userID).document(targetName).delete();
    }

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