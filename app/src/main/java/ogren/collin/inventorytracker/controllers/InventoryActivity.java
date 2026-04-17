package ogren.collin.inventorytracker.controllers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.Arrays;

import ogren.collin.inventorytracker.R;
import ogren.collin.inventorytracker.aws.services.ItemService;
import ogren.collin.inventorytracker.aws.services.LoadingCallback;
import ogren.collin.inventorytracker.aws.services.ServiceCallback;
import ogren.collin.inventorytracker.models.snowflake.inventory.ItemType;
import ogren.collin.inventorytracker.viewcomponents.DatabaseViewAdapter;

public class InventoryActivity extends AppCompatActivity {

    // Request code for the SMS_Send permission
    private static final int REQUEST_CODE = 100;

    // Declare the search view and the two recycler views that will be used.
    private SearchView inventorySearchView;
    private RecyclerView inventoryRecyclerView;
    private RecyclerView inventorySearchRecyclerView;
    private RecyclerView.AdapterDataObserver inventorySearchDataObserver;

    // Define an array to store all data.
    private ArrayList<ItemType> allData = new ArrayList<>();
    // Define an array to store filtered data.
    private final ArrayList<ItemType> filteredData = new ArrayList<>();

    // Declare the userId as a static variable.
    private static long userId;

    private static boolean askedForPermissions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Android screen setup.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from the login screen.
        Bundle extras = getIntent().getExtras();

        // Check if the userId field was included in the intent, and if it was, set the userId field to store it.
        if (extras != null) {
            userId = extras.getLong("userId");
        }

        // Give the settings icon the ability to send the user to the settings screen.
        MaterialToolbar inventoryToolbar = findViewById(R.id.inventoryToolbar);
        inventoryToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        // Add simple functionality to the search view to close it when the user presses enter.
        // In this app, the search view will be independent of, but parallel to the main recycler view.
        inventorySearchView = findViewById(R.id.inventorySearchView);
        inventorySearchView
                .getEditText()
                .setOnEditorActionListener(
                        (textView, actionId, event) -> {
                            inventorySearchView.hide();
                            return false;
                        }
                );

        // Define the recycler views as their XML counterparts
        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        inventorySearchRecyclerView = findViewById(R.id.inventorySearchRecyclerView);

        // Synchronize the inventory recycler view with the search recycler view.
        inventorySearchDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                populateDatabaseDisplay();
            }
        };

        // Search the database asynchronously when the text changes.
        inventorySearchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int _start, int _before, int _count) {
                String searchTerm = charSequence.toString();
                ItemType criteria = new ItemType(null, searchTerm, null, userId);
                ItemService.searchItems(criteria, new ServiceCallback<ItemType[]>() {
                    @Override
                    public void onSuccess(ItemType[] result) {
                        runOnUiThread(() -> {
                            filteredData.clear();
                            filteredData.addAll(Arrays.asList(result));
                            if (inventorySearchRecyclerView.getAdapter() != null) {
                                inventorySearchRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {}
                });
            }
            @Override
            public void afterTextChanged(Editable _editable) {}

            @Override
            public void beforeTextChanged(CharSequence _charSequence, int _start, int _count, int _after) {}
        });

        // Setup add item floating action button
        FloatingActionButton addToInventoryFAB = findViewById(R.id.addToInventoryFAB);
        addToInventoryFAB.setOnClickListener(fab -> {
            // The FAB should take the user to the item creation screen.
            Intent intent = new Intent(this, CreateActivity.class);
            startActivity(intent);
        });

        // Populate the list of items from the database
        populateDatabaseDisplay();

        // Request the SMS permission
        requestSMSPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // The database displays have to be repopulated when the app is resumed.
        populateDatabaseDisplay();
    }

    // Fetch the data from the database and place it into the recycler views.
    private void populateDatabaseDisplay() {
        ItemType criteria = new ItemType(null, null, null, userId);
        ItemService.getAllItems(criteria, new LoadingCallback<>(this, new ServiceCallback<ItemType[]>() {
            @Override
            public void onSuccess(ItemType[] result) {
                allData.clear();
                allData.addAll(Arrays.asList(result));

                inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(InventoryActivity.this));
                inventoryRecyclerView.setAdapter(new DatabaseViewAdapter(allData));
                inventorySearchRecyclerView.setLayoutManager(new LinearLayoutManager(InventoryActivity.this));

                // Re-filter search results if needed
                String searchTerm = inventorySearchView.getText().toString();
                ItemType searchCriteria = new ItemType(null, "%" + searchTerm + "%", null, userId);
                ItemService.searchItems(searchCriteria, new ServiceCallback<ItemType[]>() {
                    @Override
                    public void onSuccess(ItemType[] searchResult) {
                        runOnUiThread(() -> {
                            filteredData.clear();
                            filteredData.addAll(Arrays.asList(searchResult));
                            DatabaseViewAdapter filterDatabaseViewAdapter = new DatabaseViewAdapter(filteredData);
                            filterDatabaseViewAdapter.registerAdapterDataObserver(inventorySearchDataObserver);
                            inventorySearchRecyclerView.setAdapter(filterDatabaseViewAdapter);
                        });
                    }

                    @Override
                    public void onError(Exception e) {}
                });
            }

            @Override
            public void onError(Exception e) {}
        }));
    }

    // Request the SEND_SMS permission when the user first logs-in.
    private void requestSMSPermission() {
        // Check if the user has already been asked for permissions this session.
        if (askedForPermissions) {
            return;
        }
        askedForPermissions = true;

        // If the user has not granted the permission and they have not previously given their choice,
        // then ask them for the permission.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Inform the user
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.permission_request)
                .setMessage(R.string.permission_message)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.permissions_later), (dialog, which) -> {
                    // The later button will dismiss the dialog and they will be asked again.
                    dialog.dismiss();
                })
                .setPositiveButton(R.string.manage_permissions, (dialog, which) -> {
                    dialog.dismiss();
                    // If the user wishes to manage their permissions, the system permissions dialog will open.
                    ActivityCompat.requestPermissions(
                            this,
                            new String[] {Manifest.permission.SEND_SMS},
                            REQUEST_CODE
                    );
                })
                .show();
    }

    // Global getter for the user's ID.
    public static long getUserId() {
        return userId;
    }
}