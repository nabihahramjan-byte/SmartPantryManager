package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// Creates the SQLite database and its three tables, and seeds the recipes on first run.
public class PantryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table 1: what the user has at home
    private static final String CREATE_TABLE_INGREDIENT =
            "CREATE TABLE ingredient (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, quantity REAL NOT NULL, "
                    + "unit TEXT NOT NULL, expiry TEXT);";

    // Table 2: the recipe collection
    private static final String CREATE_TABLE_RECIPE =
            "CREATE TABLE recipe (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, steps TEXT NOT NULL);";

    // Table 3: the ingredients each recipe needs (many rows per recipe)
    private static final String CREATE_TABLE_RECIPE_INGREDIENT =
            "CREATE TABLE recipe_ingredient (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, "
                    + "quantity REAL NOT NULL, unit TEXT NOT NULL);";

    public PantryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Runs only the first time the database is opened.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INGREDIENT);
        db.execSQL(CREATE_TABLE_RECIPE);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENT);
        seedRecipes(db);
    }

    // Runs when DATABASE_VERSION is increased. WARNING: this deletes all data.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PantryDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion
                        + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS ingredient");
        db.execSQL("DROP TABLE IF EXISTS recipe");
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredient");
        onCreate(db);
    }

    // Saves one recipe and its ingredients.
    // Each ingredient is written as "name|quantity|unit", e.g. "egg|3|pcs".
    private void addRecipe(SQLiteDatabase db, String name, String steps, String[] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = db.insert("recipe", null, recipeValues);

        for (String line : ingredients) {
            String[] parts = line.split("\\|");
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("name", parts[0]);
            ingredientValues.put("quantity", Double.parseDouble(parts[1]));
            ingredientValues.put("unit", parts[2]);
            db.insert("recipe_ingredient", null, ingredientValues);
        }
    }

    // The 22 pre-loaded recipes.
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs",
                "1. Crack the eggs into a bowl, add the milk and whisk.\n2. Melt the butter in a pan on medium heat.\n3. Pour in the eggs and stir gently until just set.",
                new String[]{"egg|3|pcs", "butter|10|g", "milk|30|ml"});

        addRecipe(db, "Cheese Omelette",
                "1. Whisk the eggs.\n2. Melt the butter in a pan and pour in the eggs.\n3. Sprinkle the cheese on top, fold and cook for one more minute.",
                new String[]{"egg|2|pcs", "cheese|50|g", "butter|10|g"});

        addRecipe(db, "Pancakes",
                "1. Mix the flour, egg and milk into a smooth batter.\n2. Heat a lightly greased pan.\n3. Pour in a ladle of batter and cook about 2 minutes on each side.",
                new String[]{"flour|200|g", "egg|1|pcs", "milk|300|ml"});

        addRecipe(db, "French Toast",
                "1. Whisk the eggs and milk in a shallow dish.\n2. Dip each slice of bread on both sides.\n3. Fry in a hot pan until golden on both sides.",
                new String[]{"bread|4|pcs", "egg|2|pcs", "milk|100|ml"});

        addRecipe(db, "Cheese Toastie",
                "1. Butter the outside of the bread slices.\n2. Place the cheese between the slices.\n3. Toast in a pan until golden and the cheese has melted.",
                new String[]{"bread|2|pcs", "cheese|60|g", "butter|10|g"});

        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta until soft, then drain.\n2. Fry the chopped onion and garlic, add the chopped tomatoes and simmer for 10 minutes.\n3. Mix the sauce through the pasta.",
                new String[]{"pasta|200|g", "tomato|3|pcs", "onion|1|pcs", "garlic|2|pcs"});

        addRecipe(db, "Garlic Butter Pasta",
                "1. Boil the pasta until soft, then drain.\n2. Melt the butter and fry the crushed garlic for one minute.\n3. Toss the pasta in the garlic butter.",
                new String[]{"pasta|200|g", "butter|30|g", "garlic|3|pcs"});

        addRecipe(db, "Mac and Cheese",
                "1. Boil the pasta until soft, then drain.\n2. Warm the milk and melt the grated cheese into it.\n3. Stir the pasta into the cheese sauce.",
                new String[]{"pasta|250|g", "cheese|150|g", "milk|200|ml"});

        addRecipe(db, "Fried Rice",
                "1. Cook the rice and let it cool.\n2. Fry the chopped onion in the oil, then scramble in the eggs.\n3. Add the rice and stir-fry for 5 minutes.",
                new String[]{"rice|200|g", "egg|2|pcs", "onion|1|pcs", "oil|20|ml"});

        addRecipe(db, "Rice and Beans",
                "1. Cook the rice.\n2. Fry the chopped onion and tomatoes, then add the beans and simmer for 10 minutes.\n3. Serve the beans over the rice.",
                new String[]{"rice|200|g", "beans|400|g", "onion|1|pcs", "tomato|2|pcs"});

        addRecipe(db, "Chicken and Rice",
                "1. Fry the chopped onion, then add the chicken pieces and brown them.\n2. Add the rice and enough water to cover it.\n3. Cover and simmer for 20 minutes until the rice is cooked.",
                new String[]{"chicken|300|g", "rice|200|g", "onion|1|pcs"});

        addRecipe(db, "Chicken Stir-Fry",
                "1. Heat the oil in a pan and cook the chicken strips.\n2. Add the sliced onion and carrots.\n3. Stir-fry for 8 minutes until everything is cooked.",
                new String[]{"chicken|300|g", "carrot|2|pcs", "onion|1|pcs", "oil|20|ml"});

        addRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with the butter.\n3. Stir in the warm milk until creamy.",
                new String[]{"potato|4|pcs", "butter|30|g", "milk|100|ml"});

        addRecipe(db, "Roast Potatoes",
                "1. Heat the oven to 200 C.\n2. Toss the cut potatoes in the oil and salt.\n3. Roast for 40 minutes, turning once.",
                new String[]{"potato|6|pcs", "oil|30|ml", "salt|2|g"});

        addRecipe(db, "Tomato Soup",
                "1. Melt the butter and fry the chopped onion and garlic.\n2. Add the chopped tomatoes and simmer for 20 minutes.\n3. Blend until smooth.",
                new String[]{"tomato|6|pcs", "onion|1|pcs", "garlic|2|pcs", "butter|20|g"});

        addRecipe(db, "Vegetable Soup",
                "1. Chop the potatoes, carrots and onion.\n2. Cover with water in a pot and boil.\n3. Simmer for 25 minutes until soft.",
                new String[]{"potato|2|pcs", "carrot|2|pcs", "onion|1|pcs"});

        addRecipe(db, "Banana Smoothie",
                "1. Peel the bananas.\n2. Put everything in a blender.\n3. Blend until smooth and serve cold.",
                new String[]{"banana|2|pcs", "milk|300|ml", "yoghurt|100|g"});

        addRecipe(db, "Banana Pancakes",
                "1. Mash the bananas in a bowl.\n2. Mix in the eggs and flour.\n3. Fry small spoonfuls in a pan until golden on both sides.",
                new String[]{"banana|2|pcs", "egg|2|pcs", "flour|100|g"});

        addRecipe(db, "Banana Oatmeal",
                "1. Heat the milk in a pot and stir in the oats.\n2. Cook for 5 minutes, stirring.\n3. Top with sliced banana.",
                new String[]{"oats|80|g", "milk|250|ml", "banana|1|pcs"});

        addRecipe(db, "Tuna Pasta",
                "1. Boil the pasta until soft, then drain.\n2. Fry the chopped onion, then add the tuna.\n3. Mix with the pasta.",
                new String[]{"pasta|200|g", "tuna|1|pcs", "onion|1|pcs"});

        addRecipe(db, "Peanut Butter Sandwich",
                "1. Spread the peanut butter on one slice of bread.\n2. Cover with the second slice.\n3. Cut in half and serve.",
                new String[]{"bread|2|pcs", "peanut butter|30|g"});

        addRecipe(db, "Cheese and Tomato Sandwich",
                "1. Slice the tomato and the cheese.\n2. Layer them between the slices of bread.\n3. Press down and cut in half.",
                new String[]{"bread|2|pcs", "cheese|40|g", "tomato|1|pcs"});
    }
}
