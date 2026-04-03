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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import ogren.collin.inventorytracker.R;
import ogren.collin.inventorytracker.database.sqlite.InventoryDatabase;
import ogren.collin.inventorytracker.models.sqlite.inventory.ItemType;

public class EditActivity extends AppCompatActivity {

    private TextInputEditText itemNameTextInput;
    private TextInputEditText itemQuantityTextInput;

    private MaterialButton confirmEditButton;

    private String name = "";
    private Long quantity;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android screen setup.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup back button to take the user back to the inventory activity
        MaterialToolbar editToolbar = findViewById(R.id.editToolbar);
        editToolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        // Define the text inputs and confirm button to be their XML counterparts
        itemNameTextInput = findViewById(R.id.itemEditNameTextInput);
        itemQuantityTextInput = findViewById(R.id.itemEditQuantityTextInput);
        confirmEditButton = findViewById(R.id.confirmEditButton);

        // Get data from the InventoryActivity about what item has been selected
        Bundle extras = getIntent().getExtras();

        // Null check and define id, name, and quantity from intent data.
        if (extras != null) {
            id = extras.getLong("id");
            name = extras.getString("name");
            quantity = extras.getLong("quantity");
        }

        // Populate the name and quantity fields.
        populateFields(name, quantity.toString());

        // Add a text listener that makes sure that the entered name is not empty.
        itemNameTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int _start, int _before, int _count) {
                // Only let the user edit an item if they gave it a name.
                confirmEditButton.setEnabled(
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

                // Disallow the user from confirming their edit if the quantity is not a long
                confirmEditButton.setEnabled(isNumber);
            }
            @Override
            public void afterTextChanged(Editable _editable) {}

            @Override
            public void beforeTextChanged(CharSequence _charSequence, int _start, int _count, int _after) {}
        });

        // Setup delete button
        editToolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.deleteItem) {
                // Create a dialog that asks the user if they really want to delete the item type.
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Delete " + name + "?")
                        .setMessage("Are you should you would like to delete " + name + "? This action cannot be undone.")
                        .setNeutralButton(R.string.cancel, (dialog, which) -> { // Provide a cancel button.
                            dialog.cancel();
                        })
                        .setPositiveButton(R.string.delete, (dialog, which) -> { // Provide a delete button.
                            new Thread(() -> {
                                InventoryDatabase.getItemDao()
                                        .delete(
                                                InventoryDatabase.getItemDao().getById(id, InventoryActivity.getUserId())
                                        );

                                runOnUiThread(() -> {
                                    dialog.dismiss();
                                    finish();
                                });
                            }).start();
                        })
                        .show();
            }

            return true;
        });
    }

    // Place the name and quantity text into their respective fields.
    private void populateFields(String name, String quantity) {
        itemNameTextInput.setText(name);
        itemQuantityTextInput.setText(quantity);
    }

    // Write the changes to the database.
    public void confirmEdit(View view) {
        new Thread(() -> {
            // Grab the item by ID.
            ItemType item = InventoryDatabase.getItemDao().getById(id, InventoryActivity.getUserId());
            // Update the item.
            item.setName(Objects.requireNonNull(itemNameTextInput.getText()).toString());
            item.setQuantity(Long.parseLong(Objects.requireNonNull(itemQuantityTextInput.getText()).toString()));

            // Update the item.
            InventoryDatabase.getItemDao().update(item);

            // Finish the screen to return to the inventory activity.
            runOnUiThread(this::finish);
        }).start();
    }

    // Finish the activity without writing to the database.
    public void cancelEdit(View view) {
        finish();
    }
}