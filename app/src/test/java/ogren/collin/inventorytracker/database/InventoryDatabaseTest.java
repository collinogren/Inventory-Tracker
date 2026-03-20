package ogren.collin.inventorytracker.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ogren.collin.inventorytracker.models.User.User;

@RunWith(MockitoJUnitRunner.class)
public class InventoryDatabaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Context context;
    private InventoryDatabase database;

    @Before
    public void arrange() {
        context = Mockito.mock(Context.class);
        database = Room.databaseBuilder(context, InventoryDatabase.class, "inventory").build();
    }

    @Test
    public void itemType_UserInsertAndRead_IsSuccessful() {
        // Arrange
        var userTypeDao = database.userDao();
        var user = new User("username", "password");

        // Act
        userTypeDao.insert(user);
        var retrievedUser = userTypeDao.findByName("username", "password");

        // Assert
        assertEquals(user, retrievedUser);
    }

    @Test
    public void itemType_UserInsertAndDelete_IsSuccessful() {
        // Arrange
        var userTypeDao = database.userDao();
        var user = new User("username", "password");

        // Act
        userTypeDao.insert(user);
        var retrievedUser = userTypeDao.findByName("username", "password");
        userTypeDao.delete(retrievedUser);
        retrievedUser = userTypeDao.findByName("username", "password");

        // Assert
        assertNull(retrievedUser);
    }

}
