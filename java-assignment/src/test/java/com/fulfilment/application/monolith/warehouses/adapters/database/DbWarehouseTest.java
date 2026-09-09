package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DbWarehouseTest {

    @Test
    void shouldConvertDbWarehouseToDomainWarehouse() {
        LocalDateTime createdAt =
                LocalDateTime.of(2024, 1, 1, 10, 30);

        LocalDateTime archivedAt =
                LocalDateTime.of(2025, 1, 1, 10, 30);

        DbWarehouse dbWarehouse =
                new DbWarehouse();

        dbWarehouse.id = 1L;
        dbWarehouse.businessUnitCode = "MWH.TEST.001";
        dbWarehouse.location = "ZWOLLE-001";
        dbWarehouse.capacity = 100;
        dbWarehouse.stock = 20;
        dbWarehouse.createdAt = createdAt;
        dbWarehouse.archivedAt = archivedAt;

        Warehouse warehouse =
                dbWarehouse.toWarehouse();

        assertNotNull(warehouse);
        assertEquals(
                dbWarehouse.businessUnitCode,
                warehouse.businessUnitCode
        );
        assertEquals(
                dbWarehouse.location,
                warehouse.location
        );
        assertEquals(
                dbWarehouse.capacity,
                warehouse.capacity
        );
        assertEquals(
                dbWarehouse.stock,
                warehouse.stock
        );
        assertEquals(
                dbWarehouse.createdAt,
                warehouse.createdAt
        );
        assertEquals(
                dbWarehouse.archivedAt,
                warehouse.archivedAt
        );
    }

    @Test
    void shouldConvertActiveDbWarehouse() {
        DbWarehouse dbWarehouse =
                new DbWarehouse();

        dbWarehouse.businessUnitCode =
                "MWH.ACTIVE";

        dbWarehouse.location =
                "AMSTERDAM-001";

        dbWarehouse.capacity = 200;
        dbWarehouse.stock = 50;
        dbWarehouse.createdAt =
                LocalDateTime.now();
        dbWarehouse.archivedAt = null;

        Warehouse warehouse =
                dbWarehouse.toWarehouse();

        assertNotNull(warehouse);
        assertEquals(
                "MWH.ACTIVE",
                warehouse.businessUnitCode
        );
        assertEquals(
                "AMSTERDAM-001",
                warehouse.location
        );
        assertEquals(
                200,
                warehouse.capacity
        );
        assertEquals(
                50,
                warehouse.stock
        );
        assertNotNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }
}