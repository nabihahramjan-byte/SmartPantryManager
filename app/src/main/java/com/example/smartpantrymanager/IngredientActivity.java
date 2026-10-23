package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

// Screen 2: Add / Edit Ingredient.
public class IngredientActivity extends AppCompatActivity {

    private Ingredient currentIngredient;
    private EditText editName;
    private EditText editQuantity;
    private Spinner spinnerUnit;
    private TextView textViewExpiry;
    private TextView textViewTitle;
    private final String[] units = {"g", "kg", "ml", "l", "pcs"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        editName = findViewById(R.id.editTextName);
        editQuantity = findViewById(R.id.editTextQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        textViewExpiry = findViewById(R.id.textViewExpiry);
        textViewTitle = findViewById(R.id.textViewIngredientTitle);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, units);
        spinnerUnit.setAdapter(unitAdapter);

        // A new Ingredient starts with id -1; an existing one gets its id from the Intent.
        currentIngredient = new Ingredient();
        int ingredientId = getIntent().getIntExtra("ingredientId", -1);
        if (ingredientId != -1) {
            loadIngredient(ingredientId);
        }

        initSaveButton();
        initCancelButton();
        initDateButtons();
    }

    // Edit mode: read the ingredient from the database and fill the form.
    private void loadIngredient(int ingredientId) {
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            Ingredient found = ds.getIngredient(ingredientId);
            ds.close();

            if (found == null) {
                Toast.makeText(this, "Ingredient not found", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            currentIngredient = found;
            textViewTitle.setText("Edit Ingredient");
            editName.setText(currentIngredient.getName());
            editQuantity.setText(String.valueOf(currentIngredient.getQuantity()));
            for (int i = 0; i < units.length; i++) {
                if (units[i].equals(currentIngredient.getUnit())) {
                    spinnerUnit.setSelection(i);
                }
            }
            showExpiry();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading ingredient", Toast.LENGTH_LONG).show();
        }
    }

    private void showExpiry() {
        if (currentIngredient.getExpiry().isEmpty()) {
            textViewExpiry.setText("Expiry date: none");
        } else {
            textViewExpiry.setText("Expiry date: " + currentIngredient.getExpiry());
        }
    }

    private void initDateButtons() {
        Button buttonChooseDate = findViewById(R.id.buttonChooseDate);
        buttonChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(IngredientActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                // month starts at 0, so add 1. Stored as text like 2026-11-05.
                                String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                                currentIngredient.setExpiry(date);
                                showExpiry();
                            }
                        },
                        today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        Button buttonClearDate = findViewById(R.id.buttonClearDate);
        buttonClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIngredient.setExpiry("");
                showExpiry();
            }
        });
    }

    private void initCancelButton() {
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // closes this screen and goes back to the pantry list
            }
        });
    }

    private void initSaveButton() {
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIngredient();
            }
        });
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();

        // ----- Input validation -----
        if (name.isEmpty()) {
            editName.setError("Please enter an ingredient name");
            return;
        }
        if (!name.matches(".*[A-Za-z].*")) {
            editName.setError("The name must contain letters");
            return;
        }
        if (name.length() > 40) {
            editName.setError("The name is too long (max 40 characters)");
            return;
        }
        if (quantityText.isEmpty()) {
            editQuantity.setError("Please enter a quantity");
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editQuantity.setError("Please enter a valid number");
            return;
        }
        if (quantity <= 0) {
            editQuantity.setError("Quantity must be more than 0");
            return;
        }

        // ----- Copy the form values into the Ingredient object -----
        currentIngredient.setName(name);
        currentIngredient.setQuantity(quantity);
        currentIngredient.setUnit(spinnerUnit.getSelectedItem().toString());

        // ----- Save: insert if new (id = -1), update if it already exists -----
        boolean wasSuccessful;
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            if (currentIngredient.getId() == -1) {
                wasSuccessful = ds.insertIngredient(currentIngredient);
            } else {
                wasSuccessful = ds.updateIngredient(currentIngredient);
            }
            ds.close();
        } catch (Exception e) {
            wasSuccessful = false;
        }

        if (wasSuccessful) {
            Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Save failed!", Toast.LENGTH_LONG).show();
        }
    }
}
