package com.benabbou.microservices.inventory.repository;

import com.benabbou.microservices.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Custom query to check if an item exists with a specific skuCode and a minimum quantity
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Inventory i WHERE i.skuCode = :skuCode AND i.quantity >= :quantity")
    boolean existsAndQuantityIsGreaterThanEqual(@Param("skuCode") String skuCode, @Param("quantity") Integer quantity);

    // Custom query to find an inventory item by its skuCode
    @Query("SELECT i FROM Inventory i WHERE i.skuCode = :skuCode")
    Inventory findBySkuCode(@Param("skuCode") String skuCode);
}
