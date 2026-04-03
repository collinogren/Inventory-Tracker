package ogren.collin.inventorytracker.models.sqlite.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query(
        """
        SELECT * FROM user
        WHERE username = :username AND password = :password
        LIMIT 1
        """
    )
    User findByName(String username, String password);

    @Insert
    long insert(User users);

    @Delete
    void delete(User user);
}
