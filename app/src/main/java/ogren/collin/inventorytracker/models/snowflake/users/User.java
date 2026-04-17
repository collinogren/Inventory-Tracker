package ogren.collin.inventorytracker.models.snowflake.users;

import com.google.gson.annotations.SerializedName;

import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.*;

public record User(@SerializedName(USER_ID) Long id,
                   @SerializedName(USERNAME) String username,
                   @SerializedName(PASSWORD) String password) {}
