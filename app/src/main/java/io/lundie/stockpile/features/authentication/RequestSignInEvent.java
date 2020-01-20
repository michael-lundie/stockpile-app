package io.lundie.stockpile.features.authentication;

import io.lundie.stockpile.features.authentication.SignInStatusType.SignInStatusTypeDef;
import io.lundie.stockpile.features.homeview.PagingArrayStatusType;

import static io.lundie.stockpile.features.homeview.PagingArrayStatusType.PagingArrayStatusTypeDef;

/**
 * This is a wrapper class for {@link PagingArrayStatusType}. Since LiveData cannot
 * contain primitive types and the use of {@link androidx.annotation.IntDef} is preferred over Enum
 * types in android a wrapper class is necessary.
 */
public class RequestSignInEvent {
    private @SignInStatusTypeDef
    int signInStatus;

    public RequestSignInEvent(@SignInStatusTypeDef int signInStatus) {
        this.signInStatus = signInStatus;
    }

    public int getSignInStatus() { return signInStatus; }
    public void setSignInStatus(@SignInStatusTypeDef int signInStatus) {
        this.signInStatus = signInStatus;
    }
}
