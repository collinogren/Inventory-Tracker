package ogren.collin.inventorytracker.models.snowflake.users;

import com.google.gson.annotations.SerializedName;

public record User(@SerializedName(value = "id") Long id,
                   @SerializedName(value = "username") String username,
                   @SerializedName(value = "password") String password) {}
