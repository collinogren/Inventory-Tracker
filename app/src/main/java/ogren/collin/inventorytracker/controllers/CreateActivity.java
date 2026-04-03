package ogren.collin.inventorytracker.controllers;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import ogren.collin.inventorytracker.R;
import ogren.collin.inventorytracker.database.sqlite.InventoryDatabase;
import ogren.collin.inventorytracker.models.sqlite.inventory.ItemType;

public class CreateActivity extends AppCompatActivity {

    private TextInputEditText itemNameTextInput;
    private TextInputEditText itemQuantityTextInput;

    private MaterialButton createItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android screen setup.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup back button to take the user back to the inventory activity
        MaterialToolbar createToolbar = findViewById(R.id.createToolbar);
        createToolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        // Define the text inputs and create button to be their XML counterparts
        itemNameTextInput = findViewById(R.id.itemCreateNameTextInput);
        itemQuantityTextInput = findViewById(R.id.itemCreateQuantityTextInput);
        createItemButton = findViewById(R.id.confirmEditButton);

        // Add a text listener that makes sure that the entered name is not empty.
        itemNameTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int _start, int _before, int _count) {
                // Only let the user create an item if they gave it a name.
                createItemButton.setEnabled(
                        !Objects.requireNonNull(itemNameTextInput.getText()).toString().isEmpty() ||
                        !charSequence.toString().isEmpty()
                );
            }
            @Override
            public void afterTextChanged(Editable _editable) {}

            @Override
            public void beforeTextChanged(CharSequence _charSequence, int _start, int _count, int _after) {}
        });

        // Add a change listener that makes sure that the entered quantity is an integer.
        itemQuantityTextInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence charSequence, int _start, int _before, int _count) {
                // Validate that the entered quantity is a valid long
                boolean isNumber;
                try {
                    Long.parseLong(charSequence.toString());
                    isNumber = true;
                } catch (NumberFormatException nfe) {
                    isNumber = false;
                }

                // Only let the user create an item if it is a valid long.
                createItemButton.setEnabled(isNumber);
            }
            @Override
            public void afterTextChanged(Editable _editable) {}

            @Override
            public void beforeTextChanged(CharSequence _charSequence, int _start, int _count, int _after) {}
        });
    }

    // Add a new item type to the database.
    public void create(View view) {
        new Thread(() -> {
            // Gather information from the user input fields.
            String itemType = Objects.requireNonNull(itemNameTextInput.getText()).toString();
            long itemQuantity = Long.parseLong(Objects.requireNonNull(itemQuantityTextInput.getText()).toString());

            // Create the new item type in the database.
            InventoryDatabase.getItemDao()
                    .insert(new ItemType(itemType, itemQuantity));

            // Finish the creation screen.
            runOnUiThread(this::finish);
        }).start();
    }

    // Finish the creation screen without adding anything to the database.
    public void cancel(View view) {
        finish();
    }
}