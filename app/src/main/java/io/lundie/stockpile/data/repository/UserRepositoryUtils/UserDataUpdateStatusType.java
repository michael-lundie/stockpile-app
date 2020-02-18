package io.lundie.stockpile.data.repository.UserRepositoryUtils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UserDataUpdateStatusType {
    public static final int UPDATING = 1;
    public static final int SUCCESS = 2;
    public static final int FAILED = 5;

    @IntDef({UPDATING, SUCCESS, FAILED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UserDataUpdateStatusTypeDef {}

    private int userDataUpdateStatus;

    public UserDataUpdateStatusType(@UserDataUpdateStatusTypeDef int userDataUpdateStatus) {
        this.userDataUpdateStatus = userDataUpdateStatus;
    }

    @UserDataUpdateStatusTypeDef
    public int getUserDataUpdateStatus() { return this.userDataUpdateStatus; }
}
