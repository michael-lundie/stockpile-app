package io.lundie.stockpile.data.repository;

import java.util.ArrayList;

import io.lundie.stockpile.data.ItemCategory;
import io.lundie.stockpile.data.ListTypeItem;

public class FakeCategoryData {

    ArrayList<ItemCategory> itemTypesArrayList = new ArrayList<>();

    public FakeCategoryData() {
        setFakeData();
    }

    public ArrayList<ItemCategory> getItemTypesArrayList() {
        return itemTypesArrayList;
    }

    public void setItemTypesArrayList(ArrayList<ItemCategory> itemTypesArrayList) {
        this.itemTypesArrayList = itemTypesArrayList;
    }

    public void setFakeData() {
        ItemCategory itemCategoryOne = buildItemType("Staples", 1, 10, 2048, null);
        itemTypesArrayList.add(0, itemCategoryOne);
        ItemCategory itemCategoryTwo = buildItemType("Ready Meals", 1, 10, 2048, null);
        itemTypesArrayList.add(1, itemCategoryTwo);
        ItemCategory itemCategoryThree = buildItemType("Water", 1, 10, 2048, null);
        itemTypesArrayList.add(2, itemCategoryThree);
        ItemCategory itemCategoryFour = buildItemType("Medicine", 1, 10, 2048, null);
        itemTypesArrayList.add(3, itemCategoryFour);
    }

    public ItemCategory buildItemType(String itemTypeName,
                                      int superTypeId,
                                      int numberOfPiles,
                                      int totalCalories,
                                      String iconUri) {
        ItemCategory itemCategory = new ItemCategory();

        itemCategory.setItemTypeName(itemTypeName);
        itemCategory.setSuperType(superTypeId);
        itemCategory.setNumberOfPiles(numberOfPiles);
        itemCategory.setTotalCalories(totalCalories);
        itemCategory.setIconUri(iconUri);

        return itemCategory;
    }

    public ListTypeItem buildListTypeItem(String itemID,
                                           String itemName,
                                           String imageURI,
                                           int totalItems,
                                           int quantity,
                                           int counter,
                                           int expiring) {
        ListTypeItem listTypeItem = new ListTypeItem();
        listTypeItem.setItemID(itemID);
        listTypeItem.setItemName(itemName);
        listTypeItem.setImageURI(imageURI);
        listTypeItem.setTotalItemsInPile(totalItems);
        listTypeItem.setQuantity(quantity);
        listTypeItem.setCounter(counter);
        listTypeItem.setExpiring(expiring);
        return listTypeItem;
    }
}
