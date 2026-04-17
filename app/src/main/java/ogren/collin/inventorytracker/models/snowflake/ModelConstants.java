package ogren.collin.inventorytracker.models.snowflake;

import com.google.gson.Gson;

public class ModelConstants {
    // Users
    public static final String USER_ID = "userID";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    // Items
    public static final String ITEM_ID = "itemID";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_QUANTITY = "itemQuantity";

    public static final Gson GSON = new Gson();
}
