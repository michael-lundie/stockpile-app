package io.lundie.stockpile.data;

import java.util.ArrayList;

/**
 * Item POJO
 * TODO: Documentation
 */
public class ItemType {

    private String itemTypeName;
    private int superType; // int will adhere to hardcoded list of super types

    //TODO: Create separate item list POJO
    private ArrayList<ListTypeItem> itemList;
}
