package io.lundie.stockpile.utils.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import io.lundie.stockpile.data.model.firestore.ItemCategory;
import io.lundie.stockpile.data.model.firestore.ItemPile;

public class FakeData {

    private static final String LOG_TAG = FakeData.class.getSimpleName();

    private ArrayList<ItemCategory> itemCategories;
    private ArrayList<ItemPile> itemPiles;

    private final static String MAIN_MEALS = "Main Meals";
    private final static String STAPLES = "Staples";
    private final static String TINS = "Tins";
    private final static String WATER = "Water ";

    public FakeData() {
        buildFakeItemPileArray();
        buildFakeCategoriesArray(itemPiles);
    }

    public ArrayList<ItemCategory> getItemCategories() {
        return itemCategories;
    }

    public ArrayList<ItemPile> getItemPiles() {
        return itemPiles;
    }

    private void buildFakeCategoriesArray(ArrayList<ItemPile> itemPiles) {

        int mainMealsCalories = 0, staplesCalories = 0, tinsCalories = 0, waterCalories = 0;
        int mainMealsTotalPiles = 0, staplesTotalPiles = 0, tinsTotalPiles = 0, waterTotalPiles = 0;

        for (ItemPile item: itemPiles) {
                Log.i(LOG_TAG, "Fake: switch:" + item.getCategoryName());
            switch (item.getCategoryName()) {
                case(MAIN_MEALS):
                    Log.i(LOG_TAG, "Fake: switch: TRUE: Main Meals");
                    mainMealsTotalPiles++;
                    mainMealsCalories += item.getCalories() * item.getItemCount();
                    break;
                case(STAPLES):
                    Log.i(LOG_TAG, "Fake: switch: TRUE: Staples");
                    staplesTotalPiles++;
                    staplesCalories += item.getCalories() * item.getItemCount();
                    break;
                case(TINS):
                    Log.i(LOG_TAG, "Fake: switch: TRUE: Tins");
                    tinsTotalPiles++;
                    tinsCalories += item.getCalories() * item.getItemCount();
                    break;
                case(WATER):
                    Log.i(LOG_TAG, "Fake: switch: TRUE: Water");
                    waterTotalPiles++;
                    waterCalories += item.getCalories() * item.getItemCount();
                    break;
            }
        }

        itemCategories = new ArrayList<>();

        itemCategories.add(new ItemCategory(MAIN_MEALS, 1 , mainMealsTotalPiles,
                mainMealsCalories, 0));
        itemCategories.add(new ItemCategory(STAPLES, 1 , staplesTotalPiles,
                staplesCalories, 0));
        itemCategories.add(new ItemCategory(TINS, 1 , tinsTotalPiles,
                tinsCalories, 0));
        itemCategories.add(new ItemCategory(WATER, 1 , waterTotalPiles,
                waterCalories, 0));
    }

    private void buildFakeItemPileArray() {
        itemPiles = new ArrayList<>();
        ArrayList<Date> expiryList = new ArrayList<>();

        // Fake Items for category "MAIN MEALS"

        expiryList.add(FakeDataUtilHelper.parseDate("2020-10-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2020-10-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2021-02-12"));
        itemPiles.add(new ItemPile(null, "Curry", MAIN_MEALS, null,
                expiryList.size(), 314, CounterType.GRAMS,
                150, expiryList));

        expiryList.clear();

        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-31"));
        expiryList.add(FakeDataUtilHelper.parseDate("2020-04-12"));
        expiryList.add(FakeDataUtilHelper.parseDate("2020-04-12"));
        itemPiles.add(new ItemPile(null, "Beef Curry", MAIN_MEALS, null,
                expiryList.size(), 550, CounterType.GRAMS,
                340, expiryList));

        expiryList.clear();

        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-31"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        itemPiles.add(new ItemPile(null, "Hash Beef", MAIN_MEALS, null,
                expiryList.size(), 550, CounterType.GRAMS,
                340, expiryList));

        // Fake Items for category "STAPLES"

        expiryList.clear();

        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2018-12-30"));
        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-31"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        itemPiles.add(new ItemPile(null, "Plain Rice", STAPLES, null,
                expiryList.size(), 550, CounterType.GRAMS,
                340, expiryList));

        expiryList.clear();

        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        itemPiles.add(new ItemPile(null, "Canned Bread Chocolate", STAPLES, null,
                expiryList.size(), 550, CounterType.GRAMS,
                340, expiryList));

        expiryList.clear();

        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-31"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        itemPiles.add(new ItemPile(null, "Canned Bread Maple", STAPLES, null,
                expiryList.size(), 550, CounterType.GRAMS,
                340, expiryList));

        expiryList.clear();

        // Fake Items for category "TINS"

        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-31"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        itemPiles.add(new ItemPile(null, "Hagorama Tuna Small", TINS, null,
                expiryList.size(), 550, CounterType.GRAMS,
                130, expiryList));

        expiryList.clear();

        // Fake Items for category "WATER"

        expiryList.add(FakeDataUtilHelper.parseDate("2019-12-31"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        expiryList.add(FakeDataUtilHelper.parseDate("2022-04-12"));
        itemPiles.add(new ItemPile(null, "Volvic", WATER, null,
                expiryList.size(), 550, CounterType.MILLILITRES,
                2000, expiryList));
    }
}
