package io.lundie.stockpile.utils.data;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.lundie.stockpile.R;
import io.lundie.stockpile.data.model.firestore.ItemCategory;

public class CategoryBuilder {
    private final Resources resources;

    @Inject
    public CategoryBuilder(Resources resources) {
        this.resources = resources;
        createCategories();
    }

    public Map<String, Object> getCategoryObject() {
        createCategories();
        Map<String, Object> categoryObject = new HashMap<>();
        categoryObject.put("categories", createCategories());
        return categoryObject;
    }

    private ArrayList<ItemCategory> createCategories() {
        ArrayList<ItemCategory> itemCategories = new ArrayList<>();

        itemCategories.add(new ItemCategory(resources.getString(R.string.category_main_meals),
                1 , 0, 0, 0));
        itemCategories.add(new ItemCategory(resources.getString(R.string.category_staples),
                1 , 0, 0, 0));
        itemCategories.add(new ItemCategory(resources.getString(R.string.category_tins),
                1 , 0, 0, 0));
        itemCategories.add(new ItemCategory(resources.getString(R.string.category_cereals),
                1 , 0, 0, 0));
        return itemCategories;
    }
}
