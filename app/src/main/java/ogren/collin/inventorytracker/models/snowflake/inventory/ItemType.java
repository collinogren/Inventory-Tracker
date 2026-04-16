package ogren.collin.inventorytracker.models.snowflake.inventory;

import com.google.gson.annotations.SerializedName;

public record ItemType(@SerializedName(value = "itemID") Long id,
                       @SerializedName(value = "itemName") String name,
                       @SerializedName(value = "itemQuantity") Long quantity,
                       @SerializedName(value = "userID") Long userId) {}
