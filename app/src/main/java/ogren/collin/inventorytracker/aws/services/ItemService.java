package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.ITEMS_CREATE;
import static ogren.collin.inventorytracker.aws.services.APIConstants.ITEMS_GET_ALL;
import static ogren.collin.inventorytracker.aws.services.APIConstants.ITEMS_GET_ONE;
import static ogren.collin.inventorytracker.aws.services.APIConstants.RESULT;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import ogren.collin.inventorytracker.models.snowflake.inventory.ItemType;

public class ItemService {
    public static Integer createItem(ItemType itemType) {
        RestOptions request =
                RestOptions.builder()
                        .addPath(ITEMS_CREATE)
                        .addBody(GSON.toJson(itemType).getBytes(StandardCharsets.UTF_8))
                        .build();

        AtomicReference<Integer> creationStatus = new AtomicReference<>();
        Amplify.API.post(request,
                response -> {
                    try {
                        creationStatus.set((Integer) response.getData().asJSONObject().get(RESULT));
                    } catch (JSONException | ClassCastException | ArrayIndexOutOfBoundsException ignored) {
                        creationStatus.set(null);
                    }
                },
                error -> creationStatus.set(null));

        return creationStatus.get();
    }

    public static ItemType getOneItem(Long itemID) {
        ItemType item = new ItemType(itemID, null, null, null);

        RestOptions request =
                RestOptions.builder()
                        .addPath(ITEMS_GET_ONE)
                        .addBody(GSON.toJson(item).getBytes(StandardCharsets.UTF_8))
                        .build();

        AtomicReference<ItemType> itemType = new AtomicReference<>();
        Amplify.API.get(request,
                response -> {
                    try {
                        itemType.set(GSON.fromJson((String) ((Object[])
                                response.getData().asJSONObject().get(RESULT))[0],
                                ItemType.class));
                    } catch (JSONException | ClassCastException | ArrayIndexOutOfBoundsException ignored) {
                        itemType.set(null);
                    }
                },
                error -> itemType.set(null));

        return itemType.get();
    }

    public static ItemType getAllItem() {
        RestOptions request =
                RestOptions.builder()
                        .addPath(ITEMS_GET_ALL)
                        .build();

        AtomicReference<ItemType> itemType = new AtomicReference<>();
        Amplify.API.get(request,
                response -> {
                    try {
                        itemType.set(GSON.fromJson(JsonParser.parseString(response.getData().asString()),
                                ItemType.class));
                    } catch (JsonParseException | ArrayIndexOutOfBoundsException ignored) {
                        itemType.set(null);
                    }
                },
                error -> itemType.set(null));

        return itemType.get();
    }
}
