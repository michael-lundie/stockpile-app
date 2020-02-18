package io.lundie.stockpile.data.repository.UserRepositoryUtils;

import io.lundie.stockpile.data.repository.UserRepositoryUtils.UserDataUpdateStatusType.UserDataUpdateStatusTypeDef;

@FunctionalInterface
public interface UserDataUpdateStatusObserver {
    void update(@UserDataUpdateStatusTypeDef int updateStatus);
}
