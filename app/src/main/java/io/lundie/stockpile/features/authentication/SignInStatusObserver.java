package io.lundie.stockpile.features.authentication;

import static io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;

public interface SignInStatusObserver {
    void update(@SignInStatusTypeDef int signInStatus);
}
