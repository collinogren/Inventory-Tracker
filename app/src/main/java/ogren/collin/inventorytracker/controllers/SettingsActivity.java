package ogren.collin.inventorytracker.controllers;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import ogren.collin.inventorytracker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android screen setup.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the toolbar to have a back button that finishes the activity and takes the user back.
        MaterialToolbar settingsToolbar = findViewById(R.id.settingsToolbar);
        settingsToolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        // Create the checkbox with a wrapper so that the program alone can control it.
        LinearLayout allowSMSContainer = findViewById(R.id.allowSMSContainer);
        CheckBox allowSMSCheckbox = findViewById(R.id.allowSMSCheckbox);

        // Set the state of the checkbox based on the permission status.
        allowSMSCheckbox.setChecked(getApplicationContext().checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);

        // Create a permission dialog and set the checkbox's status depending on what the user does.
        var permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                allowSMSCheckbox.setChecked(true);
            } else {
                allowSMSCheckbox.setChecked(false);
            }
        });

        // Add an on click listener to the checkbox container.
        allowSMSContainer.setOnClickListener(view -> {
            // If the permission is granted, then revoke the permission.
            if (getApplicationContext().checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                revokeSelfPermissionOnKill(Manifest.permission.SEND_SMS);
                allowSMSCheckbox.setChecked(false);

                // In order for a permission to be revoked the app has to restart.
                // Therefore, inform the user of what is happening and give them a button to restart.
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Permission Revoked")
                        .setMessage("The Send SMS permission has been revoked.\nRestart the app for the change to take effect.")
                        .setCancelable(false)
                        .setPositiveButton("Restart Now", (dialog, which) -> {
                            dialog.dismiss();

                            // Create an intent to restart the activity.
                            PackageManager packageManager = getPackageManager();
                            Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                            ComponentName componentName = Objects.requireNonNull(intent).getComponent();
                            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                            mainIntent.setPackage(getPackageName());
                            startActivity(mainIntent);

                            // Relatively nicely kill the process so that it can restart.
                            android.os.Process.killProcess(android.os.Process.myPid());
                        })
                        .show();
            } else {
                // Create the permissions dialog if the user has not allowed the permission.
                permissionLauncher.launch(Manifest.permission.SEND_SMS);
            }
        });

        // Set the logout button to start the login activity and finish the current one.
        LinearLayout logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            // Flags that make the login activity the new root.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}