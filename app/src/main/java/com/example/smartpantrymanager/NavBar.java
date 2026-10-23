package com.example.smartpantrymanager;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

// Sets up the bottom navigation bar (3 buttons) so every screen can reuse the same code.
public class NavBar {

    public static final int PANTRY = 0;
    public static final int RECIPES = 1;
    public static final int SETTINGS = 2;

    // currentScreen is the button to disable (you are already on that screen).
    public static void setup(final Activity activity, int currentScreen) {
        Button buttonPantry = activity.findViewById(R.id.buttonNavPantry);
        Button buttonRecipes = activity.findViewById(R.id.buttonNavRecipes);
        Button buttonSettings = activity.findViewById(R.id.buttonNavSettings);

        // "v -> ..." is a shorter way of writing new View.OnClickListener() { onClick(View v) {...} }
        buttonPantry.setOnClickListener(v -> openScreen(activity, MainActivity.class));
        buttonRecipes.setOnClickListener(v -> openScreen(activity, SuggestedRecipesActivity.class));
        buttonSettings.setOnClickListener(v -> openScreen(activity, SettingsActivity.class));

        if (currentScreen == PANTRY) {
            buttonPantry.setEnabled(false);
        } else if (currentScreen == RECIPES) {
            buttonRecipes.setEnabled(false);
        } else if (currentScreen == SETTINGS) {
            buttonSettings.setEnabled(false);
        }
    }

    private static void openScreen(Activity activity, Class<?> screen) {
        Intent intent = new Intent(activity, screen);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
}
