package ogren.collin.inventorytracker.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import ogren.collin.inventorytracker.aws.services.LoadingCallback;
import ogren.collin.inventorytracker.aws.services.ServiceCallback;
import ogren.collin.inventorytracker.aws.services.UserService;
import ogren.collin.inventorytracker.database.sqlite.InventoryDatabase;
import ogren.collin.inventorytracker.models.snowflake.users.User;

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
            usernameTextField.setError(getString(R.string.username_cannot_be_empty));
        }

        // Check if the password is empty and if so, give an error message.
        if (password.isEmpty()) {
            passwordTextField.setError(getString(R.string.password_cannot_be_empty));
        }

        // Return early if either field is empty.
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // Attempt to login.
        UserService.login(username, password, new LoadingCallback<>(this, new ServiceCallback<>() {
            @Override
            public void onSuccess(User user) {
                // Otherwise, a user must have been found, so go to the InventoryActivity with the retrieved user ID.
                Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", user.id());
                startActivity(intent);
            }

            @Override
            public void onError(Exception e) {
                // If no user was returned or an error occurred, give an error message.
                passwordTextField.setError(getString(R.string.invalid_credentials_please_recheck_your_username_and_password_and_try_again));
            }
        }));
    }

    // Read username and password, do some simple validation, and asynchronously create a new account.
    public void handleCreateAccount(View view) {
        // Read username and password.
        String username = Objects.requireNonNull(usernameTextField.getText()).toString();
        String password = Objects.requireNonNull(passwordTextField.getText()).toString();

        // Check if the username is empty and if so, give an error message.
        if (username.isEmpty()) {
            usernameTextField.setError(getString(R.string.username_cannot_be_empty));
        }

        // Check if the password is empty and if so, give an error message.
        if (password.isEmpty()) {
            passwordTextField.setError(getString(R.string.password_cannot_be_empty));
        }

        // Return early if either field is empty.
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // Attempt to create the new account.
        UserService.register(username, password, new LoadingCallback<>(this, new ServiceCallback<>() {
            @Override
            public void onSuccess(Integer result) {
                if (result != null && result > 0) {
                    // Successful creation means a login can now happen.
                    handleLogin(view);
                } else {
                    onError(new Exception("Registration failed"));
                }
            }

            @Override
            public void onError(Exception e) {
                // Otherwise, display an error message in the username field.
                usernameTextField.setError(getString(R.string.an_account_with_that_username_already_exists_please_select_a_different_username_and_try_again));
            }
        }));
    }
}