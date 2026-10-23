package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

// Opens and closes the database and holds all the queries (the CRUD methods).
public class PantryDataSource {

    private SQLiteDatabase database;
    private PantryDBHelper dbHelper;

    public PantryDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // ---------- CREATE ----------
    public boolean insertIngredient(Ingredient ingredient) {
        boolean didSucceed = false;
        try {
            ContentValues values = new ContentValues();
            values.put("name", ingredient.getName());
            values.put("quantity", ingredient.getQuantity());
            values.put("unit", ingredient.getUnit());
            values.put("expiry", ingredient.getExpiry());

            didSucceed = database.insert("ingredient", null, values) > 0;
        } catch (Exception e) {
            // didSucceed stays false
        }
        return didSucceed;
    }

    // ---------- UPDATE ----------
    public boolean updateIngredient(Ingredient ingredient) {
        boolean didSucceed = false;
        try {
            long rowId = ingredient.getId();
            ContentValues values = new ContentValues();
            values.put("name", ingredient.getName());
            values.put("quantity", ingredient.getQuantity());
            values.put("unit", ingredient.getUnit());
            values.put("expiry", ingredient.getExpiry());

            didSucceed = database.update("ingredient", values, "_id=" + rowId, null) > 0;
        } catch (Exception e) {
            // didSucceed stays false
        }
        return didSucceed;
    }

    // ---------- DELETE ----------
    public boolean deleteIngredient(int ingredientId) {
        boolean didDelete = false;
        try {
            didDelete = database.delete("ingredient", "_id=" + ingredientId, null) > 0;
        } catch (Exception e) {
            // didDelete stays false
        }
        return didDelete;
    }

    // ---------- READ ----------
    // Returns one ingredient, or null if it was not found.
    public Ingredient getIngredient(int ingredientId) {
        Ingredient ingredient = null;
        try {
            String query = "SELECT _id, name, quantity, unit, expiry FROM ingredient WHERE _id = " + ingredientId;
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                ingredient = readIngredient(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            ingredient = null;
        }
        return ingredient;
    }

    // Returns the whole pantry. sortBy is "name" or "expiry".
    public ArrayList<Ingredient> getAllIngredients(String sortBy) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try {
            String orderBy = "name COLLATE NOCASE";
            if (sortBy.equals("expiry")) {
                // items with an expiry date first (soonest first), items without one last
                orderBy = "CASE WHEN expiry = '' THEN 1 ELSE 0 END, expiry, name COLLATE NOCASE";
            }
            String query = "SELECT _id, name, quantity, unit, expiry FROM ingredient ORDER BY " + orderBy;
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ingredients.add(readIngredient(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            ingredients = new ArrayList<>();
        }
        return ingredients;
    }

    // Turns the current cursor row into an Ingredient object.
    private Ingredient readIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getInt(0));
        ingredient.setName(cursor.getString(1));
        ingredient.setQuantity(cursor.getDouble(2));
        ingredient.setUnit(cursor.getString(3));
        String expiry = cursor.getString(4);
        if (expiry == null) {
            expiry = "";
        }
        ingredient.setExpiry(expiry);
        return ingredient;
    }

    // Returns every recipe together with its ingredient list.
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            String query = "SELECT _id, name, steps FROM recipe ORDER BY name";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(0));
                recipe.setName(cursor.getString(1));
                recipe.setSteps(cursor.getString(2));
                recipe.setIngredients(getRecipeIngredients(recipe.getId()));
                recipes.add(recipe);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            recipes = new ArrayList<>();
        }
        return recipes;
    }

    // Returns one recipe (with ingredients), or null if it was not found.
    public Recipe getRecipe(int recipeId) {
        Recipe recipe = null;
        try {
            String query = "SELECT _id, name, steps FROM recipe WHERE _id = " + recipeId;
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                recipe = new Recipe();
                recipe.setId(cursor.getInt(0));
                recipe.setName(cursor.getString(1));
                recipe.setSteps(cursor.getString(2));
                recipe.setIngredients(getRecipeIngredients(recipe.getId()));
            }
            cursor.close();
        } catch (Exception e) {
            recipe = null;
        }
        return recipe;
    }

    // Returns the ingredients a recipe needs.
    private ArrayList<RecipeIngredient> getRecipeIngredients(int recipeId) {
        ArrayList<RecipeIngredient> list = new ArrayList<>();
        String query = "SELECT name, quantity, unit FROM recipe_ingredient WHERE recipe_id = " + recipeId;
        Cursor cursor = database.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new RecipeIngredient(cursor.getString(0), cursor.getDouble(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
}
