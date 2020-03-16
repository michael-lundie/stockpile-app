package io.lundie.stockpile.utils.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.data.model.fake.ItemList;
import io.lundie.stockpile.data.model.firestore.ItemPile;

import static io.lundie.stockpile.utils.data.FakeDataUtilHelper.getRandomInt;
import static io.lundie.stockpile.utils.data.FakeDataUtilHelper.getRandomString;

public class FakeDataUtil {

    private ItemCategory itemCategory;
    private ItemList itemList;
    private ArrayList<ItemPile> itemPiles;

    public FakeDataUtil() {
        itemCategory = new ItemCategory();
        itemList = new ItemList();
        generateFakeData();
    }

    public static String TEST_USER_ID = "En6e3afY74Uwp2kofexv";


    private static final String[] ITEM_CATEGORIES_FOOD = {
            "MainMeals", "Light Meals", "Water",
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

        itemPiles = new ArrayList<>();

        Random random = new Random();
        String categoryName = getRandomString(ITEM_CATEGORIES_FOOD, random);
        int numOfFakeListItems = getRandomInt(itemCount, random);
        SUPER_TYPE fakeSuperType = SUPER_TYPE.Food;
        itemPiles = createItems(fakeSuperType, categoryName, numOfFakeListItems);


        itemCategory.setCategoryName(categoryName);
        itemList.setCategoryName(categoryName);
        itemCategory.setNumberOfPiles(numOfFakeListItems);
        itemCategory.setSuperType(1);

        int totalCalories = 0;
        for (ItemPile item : itemPiles) {
            totalCalories += item.getCalories();
        }

        itemCategory.setTotalCalories(totalCalories);

        return itemCategory;
    }

    private static ArrayList<ItemPile> createItems(SUPER_TYPE super_type,
                                                      String categoryName, int numOfItems) {
        return super_type.generateItem(categoryName, numOfItems);
    }

    private enum SUPER_TYPE {
        Food {
            @Override
            public ArrayList<ItemPile> generateItem(String categoryName, int numOfItems) {
                return buildItemsList(ITEM_NAME_FOOD, categoryName, numOfItems);
            }

            @Override
            public String getSuperTypeName() {
                return "Food";
            }
        },
        Drinks {
            @Override
            public ArrayList<ItemPile> generateItem(String categoryName, int numOfItems) {
                return buildItemsList(ITEM_NAME_DRINKS, categoryName, numOfItems);
            }

            @Override
            public String getSuperTypeName() {
                return "Drinks";
            }
        };

        abstract ArrayList<ItemPile> generateItem(String categoryName, int numOfItems);
        abstract String getSuperTypeName();
    }

    private static ArrayList<ItemPile> buildItemsList(String[] itemsNamesArray,
                                                          String categoryName, int numOfItems) {
        ArrayList<ItemPile> itemPiles = new ArrayList<>();

        while (numOfItems > 0 ) {
            Random random = new Random();
            ItemPile itemPile = new ItemPile();
            itemPile.setItemName(getRandomString(itemsNamesArray, random));
            itemPile.setCategoryName(categoryName);

            int totalItemsInPile = getRandomInt(itemCount, random);

            itemPile.setItemCount(totalItemsInPile);

            ArrayList<Date> itemExpiryArrayList = new ArrayList<>();
            RandomDate rd = new RandomDate(
                    LocalDate.of(2020, 11, 1),
                    LocalDate.of(2025, 1, 1));

            for(int i = 0; i < totalItemsInPile; i++) {

                LocalDate localDate = rd.nextDate();
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                itemExpiryArrayList.add(date);

            }
            itemPile.setExpiry(itemExpiryArrayList);
            itemPile.setQuantity(getRandomInt(itemQuantity, random));
            itemPile.setCounterType(CounterType.MILLILITRES);
            itemPile.setCalories(getRandomInt(itemCalories, random));
            itemPiles.add(itemPile);
            numOfItems--;
        }
        return itemPiles;
    }

    public static class RandomDate {
        private final LocalDate minDate;
        private final LocalDate maxDate;
        private final Random random;

        public RandomDate(LocalDate minDate, LocalDate maxDate) {
            this.minDate = minDate;
            this.maxDate = maxDate;
            this.random = new Random();
        }

        public LocalDate nextDate() {
            int minDay = (int) minDate.toEpochDay();
            int maxDay = (int) maxDate.toEpochDay();
            long randomDay = minDay + random.nextInt(maxDay - minDay);
            return LocalDate.ofEpochDay(randomDay);
        }

        @Override
        public String toString() {
            return "RandomDate{" +
                    "maxDate=" + maxDate +
                    ", minDate=" + minDate +
                    '}';
        }
    }
    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public ItemList getItemList() {
        return itemList;
    }

    public ArrayList<ItemPile> getItemPiles() {
        return itemPiles;
    }
}