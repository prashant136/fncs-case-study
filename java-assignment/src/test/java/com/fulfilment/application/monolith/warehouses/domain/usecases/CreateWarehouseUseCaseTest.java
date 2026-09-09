package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);

        useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
    }

    private Warehouse warehouse(
            String buCode,
            String location,
            int capacity,
            int stock) {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = buCode;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;

        return warehouse;
    }

    @Test
    void shouldCreateWarehouseSuccessfully() {

        Warehouse warehouse = warehouse(
                        "MWH.100",
                        "AMSTERDAM-001",
                        20,
                        10);

        Location location = new Location("AMSTERDAM-001", 5, 100);

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
                .thenReturn(location);

        when(warehouseStore.getAll())
                .thenReturn(List.of());

        useCase.create(warehouse);

        verify(warehouseStore).create(warehouse);

        assertNotNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }

    @Test
    void shouldRejectDuplicateBusinessUnitCode() {

        Warehouse existing = warehouse("MWH.001", "ZWOLLE-001", 40, 10);

        Warehouse newWarehouse = warehouse("MWH.001", "ZWOLLE-001", 20, 10);

        when(warehouseStore.findByBusinessUnitCode("MWH.001"))
                .thenReturn(existing);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> useCase.create(newWarehouse));

        assertTrue(exception.getMessage().contains("already exists"));

        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldRejectInvalidLocation() {

        Warehouse warehouse = warehouse(
                        "MWH.100",
                        "INVALID-001",
                        20,
                        10);

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("INVALID-001"))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("does not exist"));

        verify(warehouseStore, never()).create(any());
    }

    @Test
    void shouldRejectWhenMaximumWarehouseCountReached() {

        Warehouse warehouse = warehouse(
                        "MWH.100",
                        "ZWOLLE-001",
                        20,
                        10);

        Location location = new Location(
                        "ZWOLLE-001",
                        1,
                        40);

        Warehouse existing = warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        20,
                        10);

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("ZWOLLE-001"))
                .thenReturn(location);

        when(warehouseStore.getAll())
                .thenReturn(List.of(existing));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse));

        assertTrue(
                exception.getMessage()
                        .contains("Maximum number of warehouses"));

        verify(warehouseStore, never())
                .create(any());
    }

    @Test
    void shouldRejectWhenLocationCapacityExceeded() {

        Warehouse warehouse =
                warehouse(
                        "MWH.100",
                        "AMSTERDAM-001",
                        40,
                        10);

        Location location =
                new Location(
                        "AMSTERDAM-001",
                        5,
                        100);

        Warehouse existing =
                warehouse(
                        "MWH.001",
                        "AMSTERDAM-001",
                        70,
                        20);

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
                .thenReturn(location);

        when(warehouseStore.getAll())
                .thenReturn(List.of(existing));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse));

        assertTrue(
                exception.getMessage()
                        .contains("maximum capacity"));

        verify(warehouseStore, never())
                .create(any());
    }

    @Test
    void shouldRejectWhenStockExceedsCapacity() {

        Warehouse warehouse =
                warehouse(
                        "MWH.100",
                        "AMSTERDAM-001",
                        20,
                        30);

        when(warehouseStore.findByBusinessUnitCode("MWH.100"))
                .thenReturn(null);

        when(locationResolver.resolveByIdentifier("AMSTERDAM-001"))
                .thenReturn(
                        new Location(
                                "AMSTERDAM-001",
                                5,
                                100));

        when(warehouseStore.getAll())
                .thenReturn(List.of());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse));

        assertTrue(
                exception.getMessage()
                        .contains("stock"));

        verify(warehouseStore, never())
                .create(any());
    }

    @Test
    void shouldRejectNegativeStock() {

        Warehouse warehouse =
                warehouse(
                        "MWH.100",
                        "AMSTERDAM-001",
                        20,
                        -1);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse));

        assertTrue(
                exception.getMessage()
                        .contains("negative"));

        verify(warehouseStore, never())
                .create(any());
    }

    @Test
    void shouldRejectInvalidCapacity() {

        Warehouse warehouse =
                warehouse(
                        "MWH.100",
                        "AMSTERDAM-001",
                        0,
                        0);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> useCase.create(warehouse));

        assertTrue(
                exception.getMessage()
                        .contains("capacity"));

        verify(warehouseStore, never())
                .create(any());
    }
}