package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Screen 4: Recipe Detail (full ingredient list and method).
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Read the recipe id that was sent with the Intent.
        int recipeId = getIntent().getIntExtra("recipeId", -1);

        Recipe recipe = null;
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            recipe = ds.getRecipe(recipeId);
            ds.close();
        } catch (Exception e) {
            recipe = null;
        }

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView textViewRecipeTitle = findViewById(R.id.textViewDetailName);
        TextView textViewIngredients = findViewById(R.id.textViewDetailIngredients);
        TextView textViewSteps = findViewById(R.id.textViewDetailSteps);

        textViewRecipeTitle.setText(recipe.getName());

        // Build one line per ingredient, e.g. "- egg: 3 pcs"
        StringBuilder ingredientText = new StringBuilder();
        for (RecipeIngredient item : recipe.getIngredients()) {
            double q = item.getQuantity();
            String quantity = (q == Math.floor(q)) ? String.valueOf((long) q) : String.valueOf(q);
            ingredientText.append("- ").append(item.getName()).append(": ").append(quantity);
            if (!item.getUnit().equals("pcs")) {
                ingredientText.append(" ").append(item.getUnit());
            }
            ingredientText.append("\n");
        }
        textViewIngredients.setText(ingredientText.toString());
        textViewSteps.setText(recipe.getSteps());

        Button buttonBack = findViewById(R.id.buttonDetailBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
