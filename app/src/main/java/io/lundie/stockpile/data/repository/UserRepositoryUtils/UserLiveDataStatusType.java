package io.lundie.stockpile.data.repository.UserRepositoryUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UserLiveDataStatusType {
    public static final int DATA_AVAILABLE = 1;
    public static final int FETCHING = 2;
    public static final int FAILED = 3;

    @IntDef({DATA_AVAILABLE, FETCHING, FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserLiveDataStatusTypeDef {}

    private int userLiveDataStatus;

    public UserLiveDataStatusType(@UserLiveDataStatusTypeDef int userLiveDataStatus) {
        this.userLiveDataStatus = userLiveDataStatus;
    }
}
