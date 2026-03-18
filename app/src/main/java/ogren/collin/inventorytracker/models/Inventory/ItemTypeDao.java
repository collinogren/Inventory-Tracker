package ogren.collin.inventorytracker.models.Inventory;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemTypeDao {
    @Query(
        """
        SELECT rowid, * FROM `item-types`
        WHERE `user-id` = :userId
        ORDER BY `item-name` ASC;
        """
    )
    List<ItemType> getAll(long userId);

    @Query(
        """
        SELECT rowid, * FROM `item-types`
        WHERE `user-id` = :userId and rowid = :id
        LIMIT 1;
        """
    )
    ItemType getById(long id, long userId);

    @Query("SELECT COUNT(*) FROM `item-types`;")
    long count();

    @Query(
        """
        SELECT rowid, * FROM `item-types`
        WHERE `user-id` = :userId and `item-name` LIKE :searchTerm
        ORDER BY `item-name` ASC;
        """
    )
    List<ItemType> search(String searchTerm, long userId);

    @Insert
    void insert(ItemType... itemTypes);

    @Update
    void update(ItemType... itemType);

    @Delete()
    void delete(ItemType itemType);
}
