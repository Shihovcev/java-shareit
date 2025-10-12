package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void itemCreation_ShouldSetFieldsCorrectly() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setRequestId(10L);

        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.isAvailable());
        assertEquals(10L, item.getRequestId());
    }

    @Test
    void itemEqualsAndHashCode_ShouldWorkCorrectly() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Item 2");

        Item item3 = new Item();
        item3.setId(2L);
        item3.setName("Item 1");

        assertNotEquals(item1, item2);
        assertNotEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
    }

    @Test
    void itemToString_ShouldContainRelevantInformation() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");

        String toString = item.toString();

        assertTrue(toString.contains("Test Item"));
        assertTrue(toString.contains("Test Description"));
    }
}