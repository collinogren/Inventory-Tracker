package ogren.collin.inventorytracker.models.snowflake.users;

import com.google.gson.annotations.SerializedName;

import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.*;

public record User(@SerializedName(value = USER_ID) Long id,
                   @SerializedName(value = USERNAME) String username,
                   @SerializedName(value = PASSWORD) String password) {}
