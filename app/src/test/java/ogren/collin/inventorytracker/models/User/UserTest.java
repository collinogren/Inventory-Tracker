package ogren.collin.inventorytracker.models.User;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

public class UserTest {

    @Test
    public void user_standard_initialization() {
        assertDoesNotThrow(() -> new User("username", "password"));
    }

    @Test
    public void user_null_and_empty_username_initialization() {
        assertThrows(IllegalArgumentException.class, () -> new User(null, "password"));
        assertThrows(IllegalArgumentException.class, () -> new User("", "password"));
    }

    @Test
    public void user_null_and_empty_password_initialization() {
        assertThrows(IllegalArgumentException.class, () -> new User("username", null));
        assertThrows(IllegalArgumentException.class, () -> new User("username", ""));
    }

    @Test
    public void setId_and_getId_consistency_check() {
        // Arrange
        User user = new User("username", "password");
        long id = 12345;

        // Act
        user.setId(id);

        // Assert
        assertEquals(id, user.getId());
    }

    @Test
    public void setId_with_negative_value() {
        // Arrange
        User user = new User("username", "password");
        final long negativeId = -1;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> user.setId(negativeId));
    }

    @Test
    public void getUsername_standard_retrieval() {
        // Arrange
        final String username = "username";
        User user = new User(username, "password");

        // Act
        String retrievedUsername = user.getUsername();

        // Assert
        assertEquals(username, retrievedUsername);
    }

    @Test
    public void getPassword_standard_retrieval() {
        // Arrange
        final String password = "password";
        User user = new User("username", password);

        // Act
        String retrievedPassword = user.getPassword();

        // Assert
        assertEquals(password, retrievedPassword);
    }
}