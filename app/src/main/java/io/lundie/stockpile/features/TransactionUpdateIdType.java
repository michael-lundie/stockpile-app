package io.lundie.stockpile.features;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TransactionUpdateIdType {
    public static final int CAT_UPDATE_ID = 1;
    public static final int ITEM_UPDATE_ID = 2;
    public static final int TARGET_UPDATE_ID = 4;

    @IntDef({CAT_UPDATE_ID, ITEM_UPDATE_ID, TARGET_UPDATE_ID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransactionUpdateIdTypeDef {}

    private int transactionUpdateID;

    public TransactionUpdateIdType(@TransactionUpdateIdTypeDef int transactionUpdateID) {
        this.transactionUpdateID = transactionUpdateID;
    }
}
