package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.RESULT;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.Objects;

public class ServiceHelper {

    public static <T> T unwrapResult(String json, Class<T> expectedType) throws JsonParseException {
        Log.d("unwrapResult", "json: " + json);
        JsonElement result = null;
        try {
            result = getResultElementContent(json);
        } catch (Exception e) {
            Log.e("unwrapResult", Objects.requireNonNull(e.getMessage()));
        }
        Log.d("unwrapResult", "result: " + result.toString());

        if (result != null) {
            return GSON.fromJson(result, expectedType);
        } else {
            throw new JsonParseException("Result is null");
        }
    }

    public static <T> T[] unwrapResultArray(String json, Class<T[]> expectedType) throws JsonParseException {
        Log.d("unwrapResult", "json: " + json);
        JsonElement result = null;
        try {
            result = getResultElementContent(json);
        } catch (Exception e) {
            Log.e("unwrapResult", Objects.requireNonNull(e.getMessage()));
        }
        Log.d("unwrapResult", "result: " + result.toString());

        if (result != null) {
            try {
                return GSON.fromJson(result.getAsJsonArray(), expectedType);
            } catch (JsonParseException jpe) {
                Log.e("unwrapResult", Objects.requireNonNull(jpe.getMessage()));
                throw jpe;
            }
        } else {
            throw new JsonParseException("Result is null");
        }
    }

    public static <T> T unwrapResultArrayKnownSingle(String json, Class<T[]> expectedType) throws JsonParseException, ArrayIndexOutOfBoundsException {
        return unwrapResultArray(json, expectedType)[0];
    }

    private static JsonElement getResultElementContent(String json) throws JsonParseException {
        return JsonParser.parseString(json).getAsJsonObject().get(RESULT);
    }
}
