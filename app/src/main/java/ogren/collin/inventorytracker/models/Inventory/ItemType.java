package ogren.collin.inventorytracker.models.Inventory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import ogren.collin.inventorytracker.controllers.InventoryActivity;

@Fts4
@Entity(tableName = "item-types")
public class ItemType {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    private long id;

    @ColumnInfo(name = "user-id")
    private final long userId;

    @ColumnInfo(name = "item-name")
    private String name;

    @ColumnInfo(name = "item-quantity")
    private long quantity;

    @Ignore
    public ItemType(String name, long quantity) {
        if (name == null || name.isEmpty()) {
            name = "Unnamed Item";
        }

        this.userId = InventoryActivity.getUserId();
        this.name = name;
        this.quantity = quantity;
    }

    public ItemType(long id, long userId, String name, long quantity) {
        if (name == null || name.isEmpty()) {
            name = "Unnamed Item";
        }

        this.id = id;
        this.userId = userId;
        this.name = name;
        this.quantity = quantity >= 0 ? quantity : 0;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            name = "Unnamed Item";
        }

        this.name = name;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity >= 0 ? quantity : 0;
    }

    public void addQuantity(long quantity) {
        this.quantity += quantity;

        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    public void subtractQuantity(long quantity) {
        this.quantity -= quantity;

        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }
}
