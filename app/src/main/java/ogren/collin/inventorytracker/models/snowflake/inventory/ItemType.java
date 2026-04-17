package ogren.collin.inventorytracker.models.snowflake.inventory;

import com.google.gson.annotations.SerializedName;

import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.*;

public record ItemType(@SerializedName(value = ITEM_ID) Long id,
                       @SerializedName(value = ITEM_NAME) String name,
                       @SerializedName(value = ITEM_QUANTITY) Long quantity,
                       @SerializedName(value = USER_ID) Long userId) {}
