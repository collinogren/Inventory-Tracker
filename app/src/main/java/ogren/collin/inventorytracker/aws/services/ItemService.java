package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.*;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;

import java.nio.charset.StandardCharsets;

import ogren.collin.inventorytracker.models.snowflake.inventory.ItemType;

public class ItemService {
    private static final String TAG = "ItemService";

    public static void createItem(ItemType item, ServiceCallback<Integer> callback) {
        RestOptions request = RestOptions.builder()
                .addPath(ITEMS_CREATE)
                .addBody(GSON.toJson(item).getBytes(StandardCharsets.UTF_8))
                .build();

        Amplify.API.post(request,
                response -> {
                    try {
                        Integer result = ServiceHelper.unwrapResult(response.getData().asJSONObject().toString(), Integer.class);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        Log.e(TAG, "Create error: " + e.getMessage());
                        callback.onError(e);
                    }
                },
                callback::onError);
    }

    public static void getOneItem(ItemType item, ServiceCallback<ItemType> callback) {
        RestOptions request = RestOptions.builder()
                .addPath(ITEMS_GET_ONE)
                .addBody(GSON.toJson(item).getBytes(StandardCharsets.UTF_8))
                .build();

        Amplify.API.post(request,
                response -> {
                    try {
                        ItemType result = ServiceHelper.unwrapResultArrayKnownSingle(response.getData().asJSONObject().toString(), ItemType[].class);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                },
                callback::onError);
    }

    public static void getAllItems(ItemType criteria, ServiceCallback<ItemType[]> callback) {
        RestOptions request = RestOptions.builder()
                .addPath(ITEMS_GET_ALL)
                .addBody(GSON.toJson(criteria).getBytes(StandardCharsets.UTF_8))
                .build();

        Amplify.API.post(request,
                response -> {
                    try {
                        ItemType[] result = ServiceHelper.unwrapResultArray(response.getData().asJSONObject().toString(), ItemType[].class);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                },
                callback::onError);
    }

    public static void searchItems(ItemType searchCriteria, ServiceCallback<ItemType[]> callback) {
        RestOptions request = RestOptions.builder()
                .addPath(ITEMS_SEARCH)
                .addBody(GSON.toJson(searchCriteria).getBytes(StandardCharsets.UTF_8))
                .build();

        Amplify.API.post(request,
                response -> {
                    try {
                        ItemType[] result = ServiceHelper.unwrapResultArray(response.getData().asJSONObject().toString(), ItemType[].class);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                },
                callback::onError);
    }

    public static void editItems(ItemType item, ServiceCallback<Integer> callback) {
        RestOptions request = RestOptions.builder()
                .addPath(ITEMS_EDIT)
                .addBody(GSON.toJson(item).getBytes(StandardCharsets.UTF_8))
                .build();

        Amplify.API.put(request,
                response -> {
                    try {
                        Integer result = ServiceHelper.unwrapResult(response.getData().asJSONObject().toString(), Integer.class);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                },
                callback::onError);
    }

    public static void deleteItem(ItemType item, ServiceCallback<Integer> callback) {
        RestOptions request = RestOptions.builder()
                .addPath(ITEMS_DELETE)
                .addBody(GSON.toJson(item).getBytes(StandardCharsets.UTF_8))
                .build();

        Amplify.API.delete(request,
                response -> {
                    try {
                        Integer result = ServiceHelper.unwrapResult(response.getData().asJSONObject().toString(), Integer.class);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                },
                callback::onError);
    }
}
