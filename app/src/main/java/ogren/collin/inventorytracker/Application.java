package ogren.collin.inventorytracker;

import android.content.Intent;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.configuration.AmplifyOutputs;
import com.google.android.material.color.DynamicColors;

import ogren.collin.inventorytracker.controllers.LoginActivity;

public class Application extends android.app.Application {

    // Modern Android app setup.
    @Override
    public void onCreate() {
        super.onCreate();

        setupApplication();

        launchLoginActivity();
    }

    // Launch the login activity.
    public void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Any additional application setup that is required.
    private void setupApplication() {
        setupAmplify();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }

    // Setup the Amplify framework to connect to the AWS backend
    private void setupAmplify() {
        try {
            // TODO check if more information is needed for configure()
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException error) {
            Log.e("Amplify", "Could not initialize Amplify.", error);
        }
    }
}