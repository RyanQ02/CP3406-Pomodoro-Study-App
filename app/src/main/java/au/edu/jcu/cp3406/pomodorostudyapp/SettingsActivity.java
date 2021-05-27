package au.edu.jcu.cp3406.pomodorostudyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDelegate;


public class SettingsActivity extends MainActivity {
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("config", MODE_PRIVATE);

        AppCompatDelegate.setDefaultNightMode(preferences.getInt("default night mode",
                AppCompatDelegate.MODE_NIGHT_UNSPECIFIED));

        Button button = findViewById(R.id.dark_mode_button);
        button.setOnClickListener(v -> {
            switch (AppCompatDelegate.getDefaultNightMode()) {
                case AppCompatDelegate.MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;

                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

    }
    @Override
    protected void onDestroy() {
        preferences.edit().putInt("default night mode", AppCompatDelegate.getDefaultNightMode()).apply();
        super.onDestroy();
    }

    // Takes user back to the main menu.
    public void mainMenuClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}