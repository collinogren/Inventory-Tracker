package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.*;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import ogren.collin.inventorytracker.models.snowflake.users.User;

public class UserService {

    public static Integer register(String username, String password) {
        return register(new User(null, username, password));
    }

    public static Integer register(User user) {
        RestOptions request =
                RestOptions.builder()
                        .addPath(USERS_REGISTER)
                        .addBody(GSON.toJson(user).getBytes(StandardCharsets.UTF_8))
                        .build();

        AtomicReference<Integer> registerStatus = new AtomicReference<>();
        Amplify.API.post(request,
                response -> {
                    try {
                        registerStatus.set(ServiceHelper.unwrapResult(response.getData().asJSONObject().toString(), Integer.class));
                    } catch (Exception e) {
                        Log.e("UserService", Objects.requireNonNull(e.getMessage()));
                        registerStatus.set(null);
                    }
                },
                error -> registerStatus.set(null));

        return registerStatus.get();
    }

    public static User login(String username, String password) {
        return login(new User(null, username, password));
    }

    public static User login(User user) {
        RestOptions request =
                RestOptions.builder()
                        .addPath(USERS_LOGIN)
                        .addBody(GSON.toJson(user).getBytes(StandardCharsets.UTF_8))
                        .build();

        AtomicReference<User> retrievedUser = new AtomicReference<>();
        Amplify.API.post(request,
                response -> {
                    try {
                        retrievedUser.set(ServiceHelper.unwrapResultArrayKnownSingle(response.getData().asJSONObject().toString(), User[].class));
                    } catch (Exception e) {
                        Log.e("UserService", Objects.requireNonNull(e.getMessage()));
                        retrievedUser.set(null);
                    }
                },
                error -> retrievedUser.set(null));

        return retrievedUser.get();
    }
}
