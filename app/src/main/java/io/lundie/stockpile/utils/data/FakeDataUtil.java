package io.lundie.stockpile.utils.data;

import java.util.ArrayList;
import java.util.Random;

import io.lundie.stockpile.data.model.ItemCategory;
import io.lundie.stockpile.data.model.ItemList;
import io.lundie.stockpile.data.model.ListTypeItem;

import static io.lundie.stockpile.utils.data.FakeDataUtilHelper.getRandomInt;
import static io.lundie.stockpile.utils.data.FakeDataUtilHelper.getRandomString;

public class FakeDataUtil {

    private ItemCategory itemCategory;
    private ItemList itemList;

    public FakeDataUtil() {
        itemCategory = new ItemCategory();
        itemList = new ItemList();
        generateFakeData();
    }

    public static String TEST_USER_ID = "En6e3afY74Uwp2kofexv";


    private static final String[] ITEM_CATEGORIES_FOOD = {
            "Main Meals", "Light Meals", "Water",
            "Vegetables", "Pet Food", "Carbohydrates"
    };

    private static final String[] ITEM_NAME_FOOD = {
            "Rice", "Pizza", "Curry", "Steak"
    };

    private static final String[] ITEM_NAME_DRINKS = {
            "Water", "Liquid Water", "Aquarius"
    };

    private static int[] itemCount = {1, 2, 3, 4, 5, 6};

    private static int[] itemQuantity = {100, 200, 300, 400};

    private static int[] itemCalories = {1000, 2000, 3000, 4000};

    private static final String FOOD_COUNTER = "cal";

    private static final String DRINK_COUNTER = "ml";


    private ItemCategory generateFakeData() {

        ArrayList<ListTypeItem> listTypeItems = new ArrayList<>();

        Random random = new Random();
        String categoryName = getRandomString(ITEM_CATEGORIES_FOOD, random);
        int numOfFakeListItems = getRandomInt(itemCount, random);
        SUPER_TYPE fakeSuperType = SUPER_TYPE.Food;
        listTypeItems = createItems(fakeSuperType, categoryName, numOfFakeListItems);


        itemCategory.setCategoryName(categoryName);
        itemList.setCategoryName(categoryName);
        itemCategory.setNumberOfPiles(numOfFakeListItems);
        itemCategory.setSuperType(1);

        int totalCalories = 0;
        for (ListTypeItem item : listTypeItems) {
            totalCalories += item.getCalories();
        }

        itemCategory.setTotalCalories(totalCalories);
        itemList.setListItems(listTypeItems);

        return itemCategory;
    }

    private static ArrayList<ListTypeItem> createItems(SUPER_TYPE super_type,
                                                      String categoryName, int numOfItems) {
        return super_type.generateItem(categoryName, numOfItems);
    }

    private enum SUPER_TYPE {
        Food {
            @Override
            public ArrayList<ListTypeItem> generateItem(String categoryName, int numOfItems) {
                return buildItemsList(ITEM_NAME_FOOD, categoryName, numOfItems);
            }

            @Override
            public String getSuperTypeName() {
                return "Food";
            }
        },
        Drinks {
            @Override
            public ArrayList<ListTypeItem> generateItem(String categoryName, int numOfItems) {
                return buildItemsList(ITEM_NAME_DRINKS, categoryName, numOfItems);
            }

            @Override
            public String getSuperTypeName() {
                return "Drinks";
            }
        };

        abstract ArrayList<ListTypeItem> generateItem(String categoryName, int numOfItems);
        abstract String getSuperTypeName();
    }

    private static ArrayList<ListTypeItem> buildItemsList(String[] itemsNamesArray,
                                                          String categoryName, int numOfItems) {
        ArrayList<ListTypeItem> listTypeItems = new ArrayList<>();

        while (numOfItems > 0 ) {
            Random random = new Random();
            ListTypeItem listTypeItem = new ListTypeItem();
            listTypeItem.setItemName(getRandomString(itemsNamesArray, random));
            listTypeItem.setTotalItemsInPile(getRandomInt(itemCount, random));
            listTypeItem.setQuantity(getRandomInt(itemQuantity, random));
            listTypeItem.setCounter(1);
            listTypeItem.setCalories(getRandomInt(itemCalories, random));
            listTypeItems.add(listTypeItem);
            numOfItems--;
        }
        return listTypeItems;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public ItemList getItemList() {
        return itemList;
    }
}