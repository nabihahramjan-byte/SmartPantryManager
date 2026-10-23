package com.example.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

// Screen 5: Settings (saved with SharedPreferences).
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        NavBar.setup(this, NavBar.SETTINGS);
        initExpirySwitch();
        initSortRadioGroup();
    }

    // Switch: show "expiring soon" alerts in the pantry list or not.
    private void initExpirySwitch() {
        final SharedPreferences prefs = getSharedPreferences("PantrySettings", Context.MODE_PRIVATE);
        SwitchCompat switchAlerts = findViewById(R.id.switchExpiryAlerts);

        // Read the saved value and show it.
        switchAlerts.setChecked(prefs.getBoolean("expiryAlerts", true));

        // Save the new value whenever the user flips the switch.
        switchAlerts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean("expiryAlerts", b).apply();
            }
        });
    }

    // Radio buttons: sort the pantry list by name or by expiry date.
    private void initSortRadioGroup() {
        final SharedPreferences prefs = getSharedPreferences("PantrySettings", Context.MODE_PRIVATE);
        RadioGroup radioGroupSortBy = findViewById(R.id.radioGroupSortBy);
        RadioButton radioName = findViewById(R.id.radioName);
        RadioButton radioExpiry = findViewById(R.id.radioExpiry);

        String sortBy = prefs.getString("sortBy", "name");
        if (sortBy.equals("expiry")) {
            radioExpiry.setChecked(true);
        } else {
            radioName.setChecked(true);
        }

        radioGroupSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioExpiry) {
                    prefs.edit().putString("sortBy", "expiry").apply();
                } else {
                    prefs.edit().putString("sortBy", "name").apply();
                }
            }
        });
    }
}
