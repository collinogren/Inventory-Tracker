package ogren.collin.inventorytracker.aws.services;

import static ogren.collin.inventorytracker.aws.services.APIConstants.*;
import static ogren.collin.inventorytracker.models.snowflake.ModelConstants.GSON;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.google.gson.JsonParseException;

import java.nio.charset.StandardCharsets;
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
                        registerStatus.set(ServiceHelper.unwrapResult(response.getData().asString(), Integer.class));
                    } catch (JsonParseException ignored) {
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
                        .addPath(USERS_REGISTER)
                        .addBody(GSON.toJson(user).getBytes(StandardCharsets.UTF_8))
                        .build();

        AtomicReference<User> retrievedUser = new AtomicReference<>();
        Amplify.API.post(request,
                response -> {
                    try {
                        retrievedUser.set(ServiceHelper.unwrapResultArrayKnownSingle(response.getData().toString(), User[].class));
                    } catch (JsonParseException | ArrayIndexOutOfBoundsException ignored) {
                        retrievedUser.set(null);
                    }
                },
                error -> retrievedUser.set(null));

        return retrievedUser.get();
    }
}
