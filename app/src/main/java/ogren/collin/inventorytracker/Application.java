package ogren.collin.inventorytracker;

import android.content.Intent;

import com.google.android.material.color.DynamicColors;

import ogren.collin.inventorytracker.controllers.LoginActivity;

public class Application extends android.app.Application {

    // Modern Android app setup.
    @Override
    public void onCreate() {
        super.onCreate();

        DynamicColors.applyToActivitiesIfAvailable(this);

        launchLoginActivity();
    }

    // Launch the login activity.
    public void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}