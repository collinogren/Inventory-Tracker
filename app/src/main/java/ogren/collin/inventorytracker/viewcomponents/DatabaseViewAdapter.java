package ogren.collin.inventorytracker.viewcomponents;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import ogren.collin.inventorytracker.R;
import ogren.collin.inventorytracker.controllers.EditActivity;
import ogren.collin.inventorytracker.models.snowflake.inventory.ItemType;

public class DatabaseViewAdapter extends RecyclerView.Adapter<DatabaseViewAdapter.ViewHolder> {

    // Store all item types for the list
    private final ArrayList<ItemType> itemTypes;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Declare needed variables and constants.
        private final TextView nameTextView;
        private final TextView quantityTextView;
        private ItemType itemType;

        public ViewHolder(View view) {
            super(view);

            // Initialize text views and buttons.
            nameTextView = view.findViewById(R.id.itemNameTextView);
            quantityTextView = view.findViewById(R.id.itemQuantityTextView);
            MaterialButton editButton = view.findViewById(R.id.editButton);

            // When the edit button is pressed, the user will be taken to the edit activity.
            editButton.setOnClickListener(editButtonView -> {
                Intent intent = new Intent(editButtonView.getContext(), EditActivity.class);
                intent.putExtra("id", itemType.id());
                intent.putExtra("name", itemType.name());
                intent.putExtra("quantity", itemType.quantity());
                editButtonView.getContext().startActivity(intent);
            });
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getQuantityTextView() {
            return quantityTextView;
        }

        public void setItemType(ItemType itemType) {
            this.itemType = itemType;
        }
    }

    public DatabaseViewAdapter(ArrayList<ItemType> itemTypes) {
        this.itemTypes = itemTypes;
    }

    // Inflate the recycler view on creation.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_type, parent, false);

        return new ViewHolder(view);
    }

    // On binding, give each data view its required data.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNameTextView().setText(itemTypes.get(position).name());
        holder.getQuantityTextView().setText(String.valueOf(itemTypes.get(position).quantity()));
        holder.setItemType(itemTypes.get(position));
    }

    @Override
    public int getItemCount() {
        return itemTypes.size();
    }
}
