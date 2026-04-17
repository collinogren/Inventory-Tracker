package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.*;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;

import java.nio.charset.StandardCharsets;

import ogren.collin.inventorytracker.models.snowflake.users.User;

public class UserService {
    private static final String TAG = "UserService";

    public static void register(String username, String password, ServiceCallback<Integer> callback) {
        register(new User(null, username, password), callback);
    }

    public static void register(User user, ServiceCallback<Integer> callback) {
        RestOptions request =
                RestOptions.builder()
                        .addPath(USERS_REGISTER)
                        .addBody(GSON.toJson(user).getBytes(StandardCharsets.UTF_8))
                        .build();

        Amplify.API.post(request,
                response -> {
                    try {
                        Integer status = ServiceHelper.unwrapResult(response.getData().asJSONObject().toString(), Integer.class);
                        callback.onSuccess(status);
                    } catch (Exception e) {
                        Log.e(TAG, "Register parse error: " + e.getMessage());
                        callback.onError(e);
                    }
                },
                error -> {
                    Log.e(TAG, "Register API error: " + error.getMessage());
                    callback.onError(error);
                });
    }

    public static void login(String username, String password, ServiceCallback<User> callback) {
        login(new User(null, username, password), callback);
    }

    public static void login(User user, ServiceCallback<User> callback) {
        RestOptions request =
                RestOptions.builder()
                        .addPath(USERS_LOGIN)
                        .addBody(GSON.toJson(user).getBytes(StandardCharsets.UTF_8))
                        .build();

        Amplify.API.post(request,
                response -> {
                    try {
                        User retrievedUser = ServiceHelper.unwrapResultArrayKnownSingle(response.getData().asJSONObject().toString(), User[].class);
                        callback.onSuccess(retrievedUser);
                    } catch (Exception e) {
                        Log.e(TAG, "Login parse error: " + e.getMessage());
                        callback.onError(e);
                    }
                },
                error -> {
                    Log.e(TAG, "Login API error: " + error.getMessage());
                    callback.onError(error);
                });
    }
}
