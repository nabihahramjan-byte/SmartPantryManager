package com.example.smartpantrymanager;

import java.util.ArrayList;

// Model class: a recipe with its list of required ingredients.
public class Recipe {
    private int id;
    private String name;
    private String steps;
    private ArrayList<RecipeIngredient> ingredients;

    public Recipe() {
        id = -1;
        name = "";
        steps = "";
        ingredients = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public ArrayList<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(ArrayList<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}
