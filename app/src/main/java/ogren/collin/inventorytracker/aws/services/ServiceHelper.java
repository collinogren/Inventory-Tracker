package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.RESULT;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class ServiceHelper {
    private static final String TAG = "ServiceHelper";

    public static <T> T unwrapResult(String json, Class<T> expectedType) throws JsonParseException {
        Log.d(TAG, "json: " + json);
        JsonElement result = getResultElementContent(json);

        if (result == null || result.isJsonNull()) {
            throw new JsonParseException("Result element '" + RESULT + "' is null or missing");
        }

        Log.d(TAG, "result element: " + result);

        try {
            return GSON.fromJson(result, expectedType);
        } catch (Exception e) {
            Log.e(TAG, "Deserialization error: " + e.getMessage());
            throw new JsonParseException("Failed to deserialize JSON to " + expectedType.getSimpleName(), e);
        }
    }

    public static <T> T[] unwrapResultArray(String json, Class<T[]> expectedType) throws JsonParseException {
        return unwrapResult(json, expectedType);
    }

    public static <T> T unwrapResultArrayKnownSingle(String json, Class<T[]> expectedType) throws JsonParseException {
        T[] result = unwrapResultArray(json, expectedType);

        if (result == null || result.length == 0) {
            Log.e(TAG, "It's empty or sommat bro: " + result);
            throw new JsonParseException("Expected at least one element in result array, but it was empty or null");
        }

        Log.d(TAG, "array: " + result[0]);

        return result[0];
    }

    private static JsonElement getResultElementContent(String json) throws JsonParseException {
        try {
            JsonElement root = JsonParser.parseString(json);

            if (root != null && root.isJsonObject()) {
                return root.getAsJsonObject().get(RESULT);
            }

            return null;
        } catch (Exception e) {
            throw new JsonParseException("Malformed JSON or not a JSON object", e);
        }
    }
}
