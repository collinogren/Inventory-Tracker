package ogren.collin.inventorytracker.viewcomponents;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import ogren.collin.inventorytracker.R;
import ogren.collin.inventorytracker.controllers.EditActivity;
import ogren.collin.inventorytracker.database.sqlite.InventoryDatabase;
import ogren.collin.inventorytracker.models.sqlite.inventory.ItemType;

public class DatabaseViewAdapter extends RecyclerView.Adapter<DatabaseViewAdapter.ViewHolder> {

    // Store all item types for the list
    private final ArrayList<ItemType> itemTypes;

    private View view;

    // Replace an item with a new one and notify the recylcer view of the change.
    private void replaceItem(ItemType itemType) {
        for (int i = 0; i < itemTypes.size(); i++) {
            if (itemTypes.get(i).getId() == itemType.getId()) {
                itemTypes.set(i, itemType);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Declare needed variables and constants.
        private final TextView nameTextView;
        private final TextView quantityTextView;

        private ItemType itemType;

        private long adjustmentValue = 0;

        private Button positiveButton;

        private DatabaseViewAdapter viewAdapter;

        // Add some quantity to the item type.
        private void addQuantity(long quantity) {
            new Thread(() -> {
                itemType.addQuantity(quantity);
                InventoryDatabase.getItemDao().update(itemType);
                ItemType updatedItemType = InventoryDatabase.getItemDao().getById(itemType.getId(), itemType.getUserId());

                // This is not an activity, so a Handler must be used rather than runOnUiThread().
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    viewAdapter.replaceItem(updatedItemType);
                });
            }).start();
        }

        // Subtract some quantity from the item type.
        private void subtractQuantity(long quantity) {
            new Thread(() -> {
                itemType.subtractQuantity(quantity);
                InventoryDatabase.getItemDao().update(itemType);
                ItemType updatedItemType = InventoryDatabase.getItemDao().getById(itemType.getId(), itemType.getUserId());

                // This is not an activity, so a Handler must be used rather than runOnUiThread().
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    viewAdapter.replaceItem(updatedItemType);
                });
            }).start();

            // This is not an activity, so a Handler must be used rather than runOnUiThread().
            Handler mainHandler = new Handler(Looper.getMainLooper());

            // Update the database from a new thread and create a notification snackbar if needed.
            new Thread(() -> {
                // Update the database.
                InventoryDatabase.getItemDao().update(itemType);
                ItemType requeriedItemType = InventoryDatabase.getItemDao()
                        .getById(
                                itemType.getId(),
                                itemType.getUserId()
                        );

                // Create a snackbar for the user to use to find the item type that just ran out.
                if (requeriedItemType.getQuantity() == 0) {
                    mainHandler.post(() -> {
                        Snackbar snackbar = Snackbar.make(viewAdapter.view, "LOW_INVENTORY_SNACKBAR", BaseTransientBottomBar.LENGTH_LONG);
                        snackbar.setText("There are no " + itemType.getName() + " left in the inventory");
                        snackbar.setAction("Go To Item", view -> {
                            Intent intent = new Intent(view.getContext(), EditActivity.class);
                            intent.putExtra("id", requeriedItemType.getId());
                            intent.putExtra("name", requeriedItemType.getName());
                            intent.putExtra("quantity", requeriedItemType.getQuantity());
                            view.getContext().startActivity(intent);
                        });
                        snackbar.show();
                    });
                }

            }).start();
        }

        public ViewHolder(View view) {
            super(view);

            // Initialize text views and buttons.
            nameTextView = view.findViewById(R.id.itemNameTextView);
            quantityTextView = view.findViewById(R.id.itemQuantityTextView);
            MaterialButton addButton = view.findViewById(R.id.addButton);
            MaterialButton subtractButton = view.findViewById(R.id.subtractButton);
            MaterialButton editButton = view.findViewById(R.id.editButton);

            // Add button adds one to the item type.
            addButton.setOnClickListener(addButtonView -> addQuantity(1));

            // Long press opens a dialog that the user can use to add a specific quantity.
            addButton.setOnLongClickListener(addButtonView -> {
                View dialogTextLayout = createDialogInputLayout("Quantity to Add", view);
                AlertDialog dialogHandle = new MaterialAlertDialogBuilder(addButtonView.getContext())
                        .setTitle("Add a Custom Quantity")
                        .setView(dialogTextLayout)
                        .setPositiveButton("Add Quantity", (dialog, which) -> {
                            addQuantity(adjustmentValue);
                            dialog.dismiss();
                        })
                        .setNeutralButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setCancelable(true)
                        .create();
                dialogHandle.show();

                // Since there is no default value entered, the button should be disabled by default.
                positiveButton = dialogHandle.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(false);

                return true;
            });

            // Subtract button subtracts one from the item quantity.
            subtractButton.setOnClickListener(addButtonView -> subtractQuantity(1));

            // Long press opens a dialog that the user can use to subtract a specific quantity.
            subtractButton.setOnLongClickListener(addButtonView -> {
                View dialogTextLayout = createDialogInputLayout("Quantity to Subtract", view);
                AlertDialog dialogHandle = new MaterialAlertDialogBuilder(addButtonView.getContext())
                        .setTitle("Subtract a Custom Quantity")
                        .setView(dialogTextLayout)
                        .setPositiveButton("Subtract Quantity", (dialog, which) -> {
                            subtractQuantity(adjustmentValue);
                            dialog.dismiss();
                        })
                        .setNeutralButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setCancelable(true)
                        .create();
                dialogHandle.show();

                // Since there is no default value entered, the button should be disabled by default.
                positiveButton = dialogHandle.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(false);

                return true;
            });

            // When the edit button is pressed, the user will be taken to the edit activity.
            editButton.setOnClickListener(editButtonView -> {
                Intent intent = new Intent(editButtonView.getContext(), EditActivity.class);
                intent.putExtra("id", itemType.getId());
                intent.putExtra("name", itemType.getName());
                intent.putExtra("quantity", itemType.getQuantity());
                editButtonView.getContext().startActivity(intent);
            });
        }

        // Creates a layout to be used in the add / subtract dialogs.
        private View createDialogInputLayout(String hint, View view) {
            // Set the value to adjust to 0.
            adjustmentValue = 0;

            // Create a container.
            FrameLayout container = new FrameLayout(view.getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            // Adjust for display density.
            float density = view.getContext().getResources().getDisplayMetrics().density;
            layoutParams.setMargins(
                    (int) (8f * density),
                    (int) (8f * density),
                    (int) (8f * density),
                    (int) (8f * density)
            );

            // Set the container's layout parameters.
            container.setLayoutParams(layoutParams);

            // Create a Material 3 text field.
            TextInputLayout dialogTextLayout = new TextInputLayout(view.getContext());
            dialogTextLayout.setHint(hint);
            TextInputEditText dialogNumberInput = new TextInputEditText(dialogTextLayout.getContext());
            // Enforce only numbers input
            dialogNumberInput.setInputType(InputType.TYPE_CLASS_NUMBER);

            // Create a text change listener to enforce that only valid longs can be added.
            dialogNumberInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int _start, int _before, int _count) {
                    // Make sure the entered value is a valid long
                    try {
                        adjustmentValue = Long.parseLong(charSequence.toString());
                        positiveButton.setEnabled(true);
                    } catch (NumberFormatException nfe) {
                        adjustmentValue = 0;
                        positiveButton.setEnabled(false);
                    }
                }
                @Override
                public void afterTextChanged(Editable _editable) {}

                @Override
                public void beforeTextChanged(CharSequence _charSequence, int _start, int _count, int _after) {}
            });

            // Add the views to the container.
            dialogTextLayout.addView(dialogNumberInput);
            container.addView(dialogTextLayout);

            return container;
        }

        // Getters and setters:
        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getQuantityTextView() {
            return quantityTextView;
        }

        public void setItemType(ItemType itemType) {
            this.itemType = itemType;
        }

        public void setViewAdapter(DatabaseViewAdapter viewAdapter) {
            this.viewAdapter = viewAdapter;
        }
    }

    public DatabaseViewAdapter(ArrayList<ItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    // Inflate the recycler view on creation.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_type, parent, false);

        return new ViewHolder(view);
    }

    // On binding, give each data view its required data.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNameTextView().setText(itemTypes.get(position).getName());
        holder.getQuantityTextView().setText(String.valueOf(itemTypes.get(position).getQuantity()));
        holder.setItemType(itemTypes.get(position));
        holder.setViewAdapter(this);
    }

    @Override
    public int getItemCount() {
        return itemTypes.size();
    }
}
