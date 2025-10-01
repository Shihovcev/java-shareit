package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item save(Item item);

    Collection<Item> findByOwnerId(Long ownerId);

    void deleteByIdAndOwnerId(Long itemId, Long userId);

    Optional<Item> findById(Long itemId);

    @Query("SELECT i FROM Item i WHERE " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND " +
            "i.available = true")
   Collection<Item> searchAvailableItems(@Param("text") String text);
}