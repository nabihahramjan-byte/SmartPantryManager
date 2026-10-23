package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.HashMap;

// THE CORE LOGIC: the strict-matching rule.
// A recipe is only suggested if EVERY ingredient it needs is in the pantry,
// in at least the required quantity.
public class RecipeMatcher {

    // Returns only the recipes the user can make right now.
    public static ArrayList<Recipe> findMatches(ArrayList<Ingredient> pantry, ArrayList<Recipe> allRecipes) {

        // Step 1: build a "stock" list. Key = cleaned name + unit type, value = total amount.
        // If the user has the same ingredient twice (e.g. 500 g and 1 kg flour) the amounts are added together.
        HashMap<String, Double> stock = new HashMap<>();
        for (Ingredient item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double amount = toBaseQuantity(item.getQuantity(), item.getUnit());
            if (stock.containsKey(key)) {
                amount = amount + stock.get(key);
            }
            stock.put(key, amount);
        }

        // Step 2: keep only the recipes where every ingredient is available.
        ArrayList<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, stock)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    // Returns false as soon as ONE ingredient is missing or not enough.
    private static boolean canMakeRecipe(Recipe recipe, HashMap<String, Double> stock) {
        if (recipe.getIngredients().size() == 0) {
            return false;
        }
        for (RecipeIngredient needed : recipe.getIngredients()) {
            String key = makeKey(needed.getName(), needed.getUnit());

            if (!stock.containsKey(key)) {
                return false;   // ingredient is not in the pantry at all
            }
            double have = stock.get(key);
            double need = toBaseQuantity(needed.getQuantity(), needed.getUnit());
            if (have < need) {
                return false;   // not enough of it
            }
        }
        return true;            // every ingredient was found
    }

    // Cleaned name + unit type, e.g. "tomatoes" in pcs -> "tomato|pcs"
    private static String makeKey(String name, String unit) {
        return normaliseName(name) + "|" + baseUnit(unit);
    }

    // Makes "Tomatoes ", "tomato" and "TOMATO" all become "tomato".
    public static String normaliseName(String name) {
        String n = name.trim().toLowerCase();
        n = n.replaceAll("\\s+", " ");   // squash extra spaces

        if (n.endsWith("ies") && n.length() > 3) {
            n = n.substring(0, n.length() - 3) + "y";          // berries -> berry
        } else if (n.endsWith("oes") || n.endsWith("ches")
                || n.endsWith("shes") || n.endsWith("xes")) {
            n = n.substring(0, n.length() - 2);                // tomatoes -> tomato
        } else if (n.endsWith("ss")) {
            // leave it (e.g. "watercress")
        } else if (n.endsWith("s") && n.length() > 1) {
            n = n.substring(0, n.length() - 1);                // eggs -> egg
        }
        return n;
    }

    // Converts to a common unit: kg -> g, l -> ml. Other units stay the same.
    public static double toBaseQuantity(double quantity, String unit) {
        String u = unit.trim().toLowerCase();
        if (u.equals("kg") || u.equals("l")) {
            return quantity * 1000;
        }
        return quantity;
    }

    // Weights compare with weights (g), volumes with volumes (ml), items with items (pcs).
    public static String baseUnit(String unit) {
        String u = unit.trim().toLowerCase();
        if (u.equals("g") || u.equals("kg")) {
            return "g";
        }
        if (u.equals("ml") || u.equals("l")) {
            return "ml";
        }
        return "pcs";
    }
}
