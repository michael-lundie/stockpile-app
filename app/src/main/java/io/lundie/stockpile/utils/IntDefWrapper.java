package io.lundie.stockpile.utils;

/**
 * A simple extendable wrapper class for using {@link androidx.annotation.IntDef}
 * with LiveData.
 * IMPORTANT: Override get/set methods and add type definition annotations to their
 * input parameters.
 */
public abstract class IntDefWrapper {

    private int typeDef;

    public IntDefWrapper(int typeDef) {
        this.typeDef = typeDef;
    }

    public void setTypeDef(int typeDef) {
     this.typeDef = typeDef;
    }

    public int getTypeDef() {
        return typeDef;
    }
}
