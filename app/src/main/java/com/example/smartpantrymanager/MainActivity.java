package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Screen 1: the Pantry List.
public class MainActivity extends AppCompatActivity {

    private ArrayList<Ingredient> ingredients;
    private IngredientAdapter ingredientAdapter;
    private RecyclerView rvIngredients;
    private TextView textViewEmpty;
    private SwitchCompat switchDelete;

    // Runs when a row is tapped: opens the Add/Edit screen and passes the ingredient's id.
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            int ingredientId = ingredients.get(position).getId();
            Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
            intent.putExtra("ingredientId", ingredientId);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewEmpty = findViewById(R.id.textViewEmpty);
        rvIngredients = findViewById(R.id.rvIngredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        switchDelete = findViewById(R.id.switchDelete);

        NavBar.setup(this, NavBar.PANTRY);
        initAddButton();
        initDeleteSwitch();
    }

    // onResume runs every time this screen comes back to the front,
    // so the list is refreshed after adding, editing or changing settings.
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        SharedPreferences prefs = getSharedPreferences("PantrySettings", Context.MODE_PRIVATE);
        String sortBy = prefs.getString("sortBy", "name");
        boolean showAlerts = prefs.getBoolean("expiryAlerts", true);

        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            ingredients = ds.getAllIngredients(sortBy);
            ds.close();

            ingredientAdapter = new IngredientAdapter(ingredients, this);
            ingredientAdapter.setOnItemClickListener(onItemClickListener);
            ingredientAdapter.setShowExpiryAlerts(showAlerts);
            ingredientAdapter.setDelete(switchDelete.isChecked());
            rvIngredients.setAdapter(ingredientAdapter);

            updateEmptyMessage();
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving pantry items", Toast.LENGTH_LONG).show();
        }
    }

    // Shows a friendly message instead of a blank screen when the pantry is empty.
    public void updateEmptyMessage() {
        if (ingredients == null || ingredients.size() == 0) {
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    private void initAddButton() {
        Button buttonAdd = findViewById(R.id.buttonAddIngredient);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
                startActivity(intent);   // no "ingredientId" extra = add a new ingredient
            }
        });
    }

    private void initDeleteSwitch() {
        switchDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (ingredientAdapter != null) {
                    ingredientAdapter.setDelete(b);
                    ingredientAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
