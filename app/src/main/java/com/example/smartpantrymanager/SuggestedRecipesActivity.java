package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Screen 3: Suggested Recipes (only recipes the user can make right now).
public class SuggestedRecipesActivity extends AppCompatActivity {

    private ArrayList<Recipe> suggestedRecipes;
    private RecyclerView rvRecipes;
    private TextView textViewMessage;

    // Runs when a recipe is tapped: opens the detail screen and passes the recipe's id.
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", suggestedRecipes.get(position).getId());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        textViewMessage = findViewById(R.id.textViewMessage);
        rvRecipes = findViewById(R.id.rvRecipes);
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        NavBar.setup(this, NavBar.RECIPES);
    }

    // Re-run the matching every time the screen is shown, so it always reflects the current pantry.
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            ArrayList<Ingredient> pantry = ds.getAllIngredients("name");
            ArrayList<Recipe> allRecipes = ds.getAllRecipes();
            ds.close();

            // The strict-matching rule is applied here.
            suggestedRecipes = RecipeMatcher.findMatches(pantry, allRecipes);

            RecipeAdapter recipeAdapter = new RecipeAdapter(suggestedRecipes);
            recipeAdapter.setOnItemClickListener(onItemClickListener);
            rvRecipes.setAdapter(recipeAdapter);

            if (suggestedRecipes.size() == 0) {
                textViewMessage.setText("No recipes match your pantry yet - add more ingredients");
            } else {
                textViewMessage.setText("You can cook " + suggestedRecipes.size() + " recipe(s) right now:");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading recipes", Toast.LENGTH_LONG).show();
        }
    }
}
