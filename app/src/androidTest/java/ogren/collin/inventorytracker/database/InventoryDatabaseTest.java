package ogren.collin.inventorytracker.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ogren.collin.inventorytracker.models.Inventory.ItemTypeDao;
import ogren.collin.inventorytracker.models.User.UserDao;

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

    
}
