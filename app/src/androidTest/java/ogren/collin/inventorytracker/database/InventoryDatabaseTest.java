package ogren.collin.inventorytracker.database;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ogren.collin.inventorytracker.database.sqlite.InventoryDatabase;
import ogren.collin.inventorytracker.models.sqlite.inventory.ItemType;
import ogren.collin.inventorytracker.models.sqlite.inventory.ItemTypeDao;
import ogren.collin.inventorytracker.models.sqlite.user.User;
import ogren.collin.inventorytracker.models.sqlite.user.UserDao;

@RunWith(AndroidJUnit4.class)
public class InventoryDatabaseTest {
    private InventoryDatabase inventoryDatabase;
    private UserDao userDao;
    private ItemTypeDao inventoryDao;

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase.class).build();
        userDao = inventoryDatabase.userDao();
        inventoryDao = inventoryDatabase.itemTypeDao();
    }

    @After
    public void closeDB() {
        inventoryDatabase.close();
    }

    @Test
    public void write_user_and_read() {
        // Arrange
        final User user = new User("username", "password");

        // Act
        userDao.insert(user);
        final User retrievedUser = userDao.findByName("username", "password");

        // Assert
        assertThat(retrievedUser.getUsername(), equalTo(user.getUsername()));
    }

    @Test
    public void delete_user() {
        // Arrange
        final User user = new User("username", "password");
        userDao.insert(user);
        final User retrievedUser = userDao.findByName(user.getUsername(), user.getPassword());

        // Act
        userDao.delete(retrievedUser);
        final User retrievedUserAfterDelete = userDao.findByName("username", "password");

        // Assert
        assertThat(retrievedUserAfterDelete, equalTo(null));
    }

    @Test
    public void write_item_and_read() {
        // Arrange
        final User user = new User("username", "password");
        userDao.insert(user);
        final User retrievedUser = userDao.findByName(user.getUsername(), user.getPassword());
        ItemType itemType = new ItemType(1, retrievedUser.getId(), "Item Name", 10);

        // Act
        inventoryDao.insert(itemType);
        final ItemType retrievedItemType = inventoryDao.getById(itemType.getId(), retrievedUser.getId());

        // Assert
        assertThat(retrievedItemType.getName(), equalTo(itemType.getName()));
    }

    @Test
    public void delete_item() {
        // Arrange
        final User user = new User("username", "password");
        userDao.insert(user);
        final User retrievedUser = userDao.findByName(user.getUsername(), user.getPassword());

        final ItemType itemType = new ItemType(2, retrievedUser.getId(), "Item Name", 10);
        inventoryDao.insert(itemType);

        // Act
        inventoryDao.delete(itemType);
        final ItemType retrievedItemType = inventoryDao.getById(itemType.getId(), retrievedUser.getId());

        // Assert
        assertThat(retrievedItemType, equalTo(null));
    }
}
