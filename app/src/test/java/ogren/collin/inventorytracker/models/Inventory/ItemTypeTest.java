package ogren.collin.inventorytracker.models.Inventory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ItemTypeTest {

    @Test
    public void getId_retrieval_check() {
        // Arrange
        final long id = 12345;
        final long userId = 12345;
        final String itemName = "Item Name";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(id, userId, itemName, itemQuantity);

        // Act
        final long retrievedId = item.getId();

        // Assert
        assertEquals(item.getId(), retrievedId);
    }

    @Test
    public void getUserId_retrieval_check() {
        // Arrange
        final long id = 12345;
        final long userId = 12345;
        final String itemName = "Item Name";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(id, userId, itemName, itemQuantity);

        // Act
        final long retrievedUserId = item.getUserId();

        // Assert
        assertEquals(item.getUserId(), retrievedUserId);
    }

    @Test
    public void getName_retrieval_check() {
        // Arrange
        final long id = 12345;
        final long userId = 12345;
        final String itemName = "Item Name";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(id, userId, itemName, itemQuantity);

        // Act
        final String retrievedItemName = item.getName();

        // Assert
        assertEquals(item.getName(), retrievedItemName);
    }

    @Test
    public void setName_valid_string_update() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(itemName, itemQuantity);
        final String newName = "New Item Name";

        // Act
        item.setName(newName);

        // Assert
        assertEquals(newName, item.getName());
    }

    @Test
    public void setName_null_input_handling() {
        // Arrange
        final String itemName = "Item Name";
        final String defaultItemName = "Unnamed Item";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.setName(null);

        // Assert
        assertEquals(defaultItemName, item.getName());
    }

    @Test
    public void setName_empty_string_handling() {
        // Arrange
        final String itemName = "Item Name";
        final String defaultItemName = "Unnamed Item";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.setName("");

        // Assert
        assertEquals(defaultItemName, item.getName());
    }

    @Test
    public void getQuantity_retrieval_check() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 12345;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act and Assert
        assertEquals(itemQuantity, item.getQuantity());
    }

    @Test
    public void setQuantity_positive_value_assignment() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 0;
        final long newItemQuantity = 12345;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.setQuantity(newItemQuantity);

        // Assert
        assertEquals(newItemQuantity, item.getQuantity());
    }

    @Test
    public void setQuantity_negative_value_clamping() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 12345;
        final long newItemQuantity = -1;
        final long expectedItemQuantity = 0;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.setQuantity(newItemQuantity);

        // Assert
        assertEquals(expectedItemQuantity, item.getQuantity());
    }

    @Test
    public void addQuantity_standard_increment() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 0;
        final long newQuantity = 1;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.addQuantity(1);

        // Assert
        assertEquals(newQuantity, item.getQuantity());
    }

    @Test
    public void addQuantity_with_negative_input() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 2;
        final long newQuantity = 1;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.addQuantity(-1);

        // Assert
        assertEquals(newQuantity, item.getQuantity());
    }

    @Test
    public void addQuantity_with_negative_input_floor_enforcement() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 0;
        final long newQuantity = 0;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.addQuantity(-1);

        // Assert
        assertEquals(newQuantity, item.getQuantity());
    }

    @Test
    public void subtractQuantity_standard_decrement() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 1;
        final long newQuantity = 0;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.subtractQuantity(1);

        // Assert
        assertEquals(newQuantity, item.getQuantity());
    }

    @Test
    public void subtractQuantity_floor_enforcement() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 0;
        final long newQuantity = 0;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.subtractQuantity(1);

        // Assert
        assertEquals(newQuantity, item.getQuantity());
    }

    @Test
    public void subtractQuantity_with_negative_input() {
        // Arrange
        final String itemName = "Item Name";
        final long itemQuantity = 0;
        final long newQuantity = 1;
        ItemType item = new ItemType(itemName, itemQuantity);

        // Act
        item.subtractQuantity(-1);

        // Assert
        assertEquals(newQuantity, item.getQuantity());
    }
}