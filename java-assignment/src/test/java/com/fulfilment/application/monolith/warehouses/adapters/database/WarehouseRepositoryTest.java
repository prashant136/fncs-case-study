package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository repository;

    @Test
    @TestTransaction
    void shouldCreateWarehouse() {

        System.out.println(">>> TEST STARTED");

        Warehouse warehouse = createWarehouse(
                "TEST.CREATE",
                "ZWOLLE-001",
                100,
                10
        );

        System.out.println(">>> BEFORE CREATE");

        repository.create(warehouse);

        System.out.println(">>> AFTER CREATE");

        Warehouse result =
                repository.findByBusinessUnitCode("TEST.CREATE");

        System.out.println(">>> AFTER FIND");

        assertNotNull(result);
        assertEquals("TEST.CREATE", result.businessUnitCode);
        assertEquals("ZWOLLE-001", result.location);
        assertEquals(100, result.capacity);
        assertEquals(10, result.stock);
        assertEquals(warehouse.createdAt, result.createdAt);
        assertNull(result.archivedAt);
    }

    @Test
    @TestTransaction
    void shouldFindWarehouseByBusinessUnitCode() {
        Warehouse warehouse = createWarehouse(
                "TEST.FIND",
                "AMSTERDAM-001",
                200,
                50
        );

        repository.create(warehouse);

        Warehouse result =
                repository.findByBusinessUnitCode("TEST.FIND");

        assertNotNull(result);
        assertEquals("TEST.FIND", result.businessUnitCode);
        assertEquals("AMSTERDAM-001", result.location);
        assertEquals(200, result.capacity);
        assertEquals(50, result.stock);
    }

    @Test
    @TestTransaction
    void shouldReturnNullWhenWarehouseDoesNotExist() {
        Warehouse result =
                repository.findByBusinessUnitCode(
                        "TEST.DOES.NOT.EXIST"
                );

        assertNull(result);
    }

    @Test
    @TestTransaction
    void shouldGetAllActiveWarehouses() {
        Warehouse warehouse1 = createWarehouse(
                "TEST.ACTIVE.1",
                "ZWOLLE-001",
                100,
                10
        );

        Warehouse warehouse2 = createWarehouse(
                "TEST.ACTIVE.2",
                "AMSTERDAM-001",
                200,
                50
        );

        repository.create(warehouse1);
        repository.create(warehouse2);

        List<Warehouse> result =
                repository.getAll();

        assertNotNull(result);

        assertTrue(
                result.stream()
                        .anyMatch(w ->
                                "TEST.ACTIVE.1".equals(
                                        w.businessUnitCode
                                )
                        )
        );

        assertTrue(
                result.stream()
                        .anyMatch(w ->
                                "TEST.ACTIVE.2".equals(
                                        w.businessUnitCode
                                )
                        )
        );
    }

    @Test
    @TestTransaction
    void shouldExcludeArchivedWarehousesFromGetAll() {
        Warehouse activeWarehouse = createWarehouse(
                "TEST.ACTIVE",
                "ZWOLLE-001",
                100,
                10
        );

        Warehouse archivedWarehouse = createWarehouse(
                "TEST.ARCHIVED",
                "ZWOLLE-001",
                100,
                20
        );

        archivedWarehouse.archivedAt =
                LocalDateTime.now();

        repository.create(activeWarehouse);
        repository.create(archivedWarehouse);

        List<Warehouse> result =
                repository.getAll();

        assertTrue(
                result.stream()
                        .anyMatch(w ->
                                "TEST.ACTIVE".equals(
                                        w.businessUnitCode
                                )
                        )
        );

        assertFalse(
                result.stream()
                        .anyMatch(w ->
                                "TEST.ARCHIVED".equals(
                                        w.businessUnitCode
                                )
                        )
        );
    }

    @Test
    @TestTransaction
    void shouldUpdateWarehouse() {
        Warehouse warehouse = createWarehouse(
                "TEST.UPDATE",
                "ZWOLLE-001",
                100,
                10
        );

        repository.create(warehouse);

        warehouse.location = "AMSTERDAM-001";
        warehouse.capacity = 250;
        warehouse.stock = 75;

        repository.update(warehouse);

        Warehouse result =
                repository.findByBusinessUnitCode(
                        "TEST.UPDATE"
                );

        assertNotNull(result);
        assertEquals(
                "AMSTERDAM-001",
                result.location
        );
        assertEquals(250, result.capacity);
        assertEquals(75, result.stock);
        assertEquals(
                warehouse.createdAt,
                result.createdAt
        );
    }

    @Test
    @TestTransaction
    void shouldUpdateWarehouseArchiveDate() {
        Warehouse warehouse = createWarehouse(
                "TEST.ARCHIVE",
                "ZWOLLE-001",
                100,
                10
        );

        repository.create(warehouse);

        LocalDateTime archivedAt =
                LocalDateTime.now();

        warehouse.archivedAt = archivedAt;

        repository.update(warehouse);

        Warehouse result =
                repository.findByBusinessUnitCode(
                        "TEST.ARCHIVE"
                );

        assertNotNull(result);
        assertEquals(
                archivedAt,
                result.archivedAt
        );
    }

    @Test
    @TestTransaction
    void shouldThrowExceptionWhenUpdatingUnknownWarehouse() {
        Warehouse warehouse = createWarehouse(
                "TEST.UNKNOWN.UPDATE",
                "ZWOLLE-001",
                100,
                10
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> repository.update(warehouse)
                );

        assertTrue(
                exception.getMessage()
                        .contains("TEST.UNKNOWN.UPDATE")
        );
    }

    @Test
    @TestTransaction
    void shouldRemoveWarehouse() {
        Warehouse warehouse = createWarehouse(
                "TEST.REMOVE",
                "ZWOLLE-001",
                100,
                10
        );

        repository.create(warehouse);

        Warehouse created =
                repository.findByBusinessUnitCode(
                        "TEST.REMOVE"
                );

        assertNotNull(created);

        repository.remove(created);

        Warehouse result =
                repository.findByBusinessUnitCode(
                        "TEST.REMOVE"
                );

        assertNull(result);
    }

    @Test
    @TestTransaction
    void shouldNotFailWhenRemovingUnknownWarehouse() {
        Warehouse warehouse = createWarehouse(
                "TEST.UNKNOWN.REMOVE",
                "ZWOLLE-001",
                100,
                10
        );

        assertDoesNotThrow(
                () -> repository.remove(warehouse)
        );
    }

    private Warehouse createWarehouse(
            String businessUnitCode,
            String location,
            int capacity,
            int stock) {

        Warehouse warehouse =
                new Warehouse();

        warehouse.businessUnitCode =
                businessUnitCode;

        warehouse.location =
                location;

        warehouse.capacity =
                capacity;

        warehouse.stock =
                stock;

        warehouse.createdAt =
                LocalDateTime.now();

        warehouse.archivedAt = null;

        return warehouse;
    }
}