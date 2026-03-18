package ogren.collin.inventorytracker.models.User;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"username"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    private long id;

    @ColumnInfo(name = "username")
    private final String username;

    @ColumnInfo(name = "password")
    private final String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
