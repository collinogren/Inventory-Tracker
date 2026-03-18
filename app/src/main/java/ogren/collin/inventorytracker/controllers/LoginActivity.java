package ogren.collin.inventorytracker.controllers;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import ogren.collin.inventorytracker.R;
import ogren.collin.inventorytracker.database.InventoryDatabase;
import ogren.collin.inventorytracker.models.User.User;

// Activity to provide login functionality for users.
public class LoginActivity extends AppCompatActivity {

    // Declare username and password fields.
    private TextInputEditText usernameTextField;
    private TextInputEditText passwordTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android window setup
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Define the username and password fields as the ID given in the XML
        usernameTextField = findViewById(R.id.usernameTextField);
        passwordTextField = findViewById(R.id.passwordTextField);

        // Add an on focus change listener to both fields to remove any error messages if the user
        // attempts to modify a field.
        usernameTextField.setOnFocusChangeListener((view, focus) -> {
            usernameTextField.setError(null);
            passwordTextField.setError(null);
        });

        passwordTextField.setOnFocusChangeListener((view, focus) -> {
            usernameTextField.setError(null);
            passwordTextField.setError(null);
        });

        // Build the database using Room so that user credentials can be checked.
        InventoryDatabase inventoryDatabase = Room.databaseBuilder(getApplicationContext(), InventoryDatabase.class, "inventory").build();
        InventoryDatabase.buildGlobalDatabaseAccess(inventoryDatabase);
    }

    // Read username and password, do some simple validation, and send the data to an async login function
    public void handleLogin(View view) {
        // Get the username and password.
        String username = Objects.requireNonNull(usernameTextField.getText()).toString();
        String password = Objects.requireNonNull(passwordTextField.getText()).toString();

        // Check if the username is empty and if so, give an error message.
        if (username.isEmpty()) {
            usernameTextField.setError("Username cannot be empty.");
        }

        // Check if the password is empty and if so, give an error message.
        if (password.isEmpty()) {
            passwordTextField.setError("Password cannot be empty.");
        }

        // Return early if either field is empty.
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // Attempt to login.
        new Thread(() -> {
            loginAsync(username, password);
        }).start();
    }

    // Read username and password, do some simple validation, and asynchronously create a new account.
    public void handleCreateAccount(View view) {
        // Read username and password.
        String username = Objects.requireNonNull(usernameTextField.getText()).toString();
        String password = Objects.requireNonNull(passwordTextField.getText()).toString();

        // Check if the username is empty and if so, give an error message.
        if (username.isEmpty()) {
            usernameTextField.setError("Username cannot be empty.");
        }

        // Check if the password is empty and if so, give an error message.
        if (password.isEmpty()) {
            passwordTextField.setError("Password cannot be empty.");
        }

        // Return early if either field is empty.
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // Attempt to create the new account.
        new Thread(() -> {
            boolean creationSuccessful;
            // Attempt to insert the new user credentials.
            try {
                long result = InventoryDatabase.getUserDao().insert(
                        new User(
                                username,
                                password
                        )
                );
                // If the number of rows modified is greater than 0 it was a success.
                creationSuccessful = result > 0;
            } catch (SQLiteConstraintException constraintException) {
                // If there was an issue, such as a username already existing, then the creation failed.
                creationSuccessful = false;
            }

            if (creationSuccessful) { // Successful creation means a login can now happen.
                loginAsync(username, password);
            } else { // Otherwise, display an error message in the username field.
                runOnUiThread(() -> {
                    usernameTextField.setError("An account with that username already exists. Please select a different username and try again.");
                });
            }
        }).start();
    }

    // Async function that attempts to log the user into their account.
    private void loginAsync(String username, String password) {
        // Get the user that has a matching username and password.
        User user = InventoryDatabase.getUserDao().findByName(username, password);

        // Check if a user was returned.
        if (user == null) {
            // If no user was returned, give an error message.
            runOnUiThread(() -> passwordTextField.setError("Invalid credentials. Please recheck your username and password and try again."));
            return;
        }

        // Otherwise, a user must have been found, so go to the InventoryActivity with the retrieved user ID.
        runOnUiThread(() -> {
            Intent intent = new Intent(this, InventoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("userId", user.getId());
            startActivity(intent);
        });
    }
}