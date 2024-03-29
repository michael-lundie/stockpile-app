package io.lundie.stockpile.features.authentication;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SignInStatusType {
    public static final int ATTEMPTING_SIGN_IN = 0;
    public static final int SUCCESS = 1;
    public static final int SUCCESS_ANON = 2;
    public static final int FAIL_AUTH = 3;
    public static final int REQUEST_SIGN_IN = 4;
    public static final int SIGN_OUT = 5;

    @IntDef({ATTEMPTING_SIGN_IN, SUCCESS, SUCCESS_ANON, FAIL_AUTH, REQUEST_SIGN_IN, SIGN_OUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SignInStatusTypeDef {}

    private int signInStatus;

    @SignInStatusTypeDef
    public int getSignInStatus() { return  this.signInStatus; }
}
