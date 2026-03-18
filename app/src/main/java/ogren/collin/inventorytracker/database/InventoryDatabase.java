package ogren.collin.inventorytracker.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ogren.collin.inventorytracker.models.Inventory.ItemType;
import ogren.collin.inventorytracker.models.Inventory.ItemTypeDao;
import ogren.collin.inventorytracker.models.User.User;
import ogren.collin.inventorytracker.models.User.UserDao;

// Database to store both users and items.
@Database(entities = {User.class, ItemType.class}, version = 1)
public abstract class InventoryDatabase extends RoomDatabase {

    // Tables
    public abstract UserDao userDao();
    public abstract ItemTypeDao itemTypeDao();

    // Static DAO variables. Can only be mutated from this class.
    private static ItemTypeDao itemDao;
    private static UserDao userDao;

    // Builds the DAOs so that they may be accessed from anywhere.
    public static void buildGlobalDatabaseAccess(InventoryDatabase inventoryDatabase) {
        itemDao = inventoryDatabase.itemTypeDao();
        userDao = inventoryDatabase.userDao();
    }

    // Global access to the database.
    public static ItemTypeDao getItemDao() {
        return itemDao;
    }

    public static UserDao getUserDao() {
        return userDao;
    }
}
