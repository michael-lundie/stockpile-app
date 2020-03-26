package io.lundie.stockpile.features.stocklist.manageitem;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ImageUpdateStatusType {
    public static final String AVAILABLE = "AVAILABLE";
    public static final String UPLOADING = "UPLOADING";
    public static final String FAILED = "FAILED";
    public static final String NONE = "NONE";

    @StringDef({AVAILABLE, UPLOADING, FAILED, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageUpdateStatusTypeDef {}

    private int imageUpdateStatus;

    public ImageUpdateStatusType(@ImageUpdateStatusTypeDef int imageUpdateStatus) {
        this.imageUpdateStatus = imageUpdateStatus;
    }

    @ImageUpdateStatusTypeDef
    public int getImageUpdateStatus() { return this.imageUpdateStatus; }
}