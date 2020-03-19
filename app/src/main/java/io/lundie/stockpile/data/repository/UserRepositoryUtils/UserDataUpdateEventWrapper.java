package io.lundie.stockpile.data.repository.UserRepositoryUtils;

import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType.UserDataUpdateStatusTypeDef;
import io.lundie.stockpile.features.stocklist.manageitem.ImageUpdateStatusType.ImageUpdateStatusTypeDef;
import io.lundie.stockpile.utils.IntDefEventWrapper;

/**
 * This is a wrapper class for {@link ImageUpdateStatusTypeDef}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
public class UserDataUpdateEventWrapper extends IntDefEventWrapper {

    private @UserDataUpdateStatusTypeDef int updateStatus;

    int getUpdateStatus() {
        return updateStatus;
    }

    void setUpdateStatus(@UserDataUpdateStatusTypeDef int updateStatus) {
        this.updateStatus = updateStatus;
    }
}