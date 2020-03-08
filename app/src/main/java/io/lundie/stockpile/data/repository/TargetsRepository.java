package io.lundie.stockpile.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

import io.lundie.stockpile.data.FirestoreQueryLiveData;
import io.lundie.stockpile.data.model.Target;
import io.lundie.stockpile.utils.AppExecutors;
import timber.log.Timber;

public class TargetsRepository extends BaseRepository{
    private FirebaseFirestore firestore;
    private AppExecutors appExecutors;
    private FirestoreQueryLiveData targetsLiveData;

    @Inject
    TargetsRepository(FirebaseFirestore firebaseFirestore, AppExecutors appExecutors) {
        this.firestore = firebaseFirestore;
        this.appExecutors = appExecutors;
    }

    public LiveData<QuerySnapshot> getTargets(@NonNull String userID) {
        if(targetsLiveData == null || targetsLiveData.getValue() == null) {
            Query targetsQuery = firestore.collection("users")
                    .document(userID)
                    .collection("targets")
                    .limit(10);
            targetsLiveData = new FirestoreQueryLiveData(targetsQuery);
        }
        return targetsLiveData;
    }

    public void addTarget(String userID, Target newTarget) {
        Timber.e("Target data is: %s", newTarget.getTargetName());
        setTargetData(userID, newTarget, false, null);
    }

    public void updateTarget(String userID, Target updatedTarget, String originalTitle) {
        //TODO: implement update
    }

    private void setTargetData(String userID, Target targetData, Boolean isUpdate,
                               @Nullable String originalTitle) {

        DocumentReference documentReference = firestore.collection("users")
                .document(userID).collection("targets")
                .document(targetData.getTargetName());

        documentReference.set(targetData);
    }
}
