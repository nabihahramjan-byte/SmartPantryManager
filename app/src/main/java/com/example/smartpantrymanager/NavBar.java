package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.Button;

// Sets up the bottom navigation bar (3 buttons) so every screen can reuse the same code.
public class NavBar {

    public static final int PANTRY = 0;
    public static final int RECIPES = 1;
    public static final int SETTINGS = 2;

    // currentScreen is the screen you are on: its button is shown in dark purple.
    public static void setup(final Activity activity, int currentScreen) {
        Button buttonPantry = activity.findViewById(R.id.buttonNavPantry);
        Button buttonRecipes = activity.findViewById(R.id.buttonNavRecipes);
        Button buttonSettings = activity.findViewById(R.id.buttonNavSettings);

        // Colour the buttons: the current screen is dark purple, the other two are light purple.
        styleButton(buttonPantry, currentScreen == PANTRY);
        styleButton(buttonRecipes, currentScreen == RECIPES);
        styleButton(buttonSettings, currentScreen == SETTINGS);

        // Only the buttons for the OTHER screens do anything when tapped.
        // "v -> ..." is a shorter way of writing new View.OnClickListener() { onClick(View v) {...} }
        if (currentScreen != PANTRY) {
            buttonPantry.setOnClickListener(v -> openScreen(activity, MainActivity.class));
        }
        if (currentScreen != RECIPES) {
            buttonRecipes.setOnClickListener(v -> openScreen(activity, SuggestedRecipesActivity.class));
        }
        if (currentScreen != SETTINGS) {
            buttonSettings.setOnClickListener(v -> openScreen(activity, SettingsActivity.class));
        }
    }

    // Dark purple + white text when selected, light purple + dark text when not.
    private static void styleButton(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6750A4")));
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8DEF8")));
            button.setTextColor(Color.parseColor("#4A4458"));
        }
    }

    private static void openScreen(Activity activity, Class<?> screen) {
        Intent intent = new Intent(activity, screen);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
}
