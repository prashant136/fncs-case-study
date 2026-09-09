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

class ReplaceWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {

        warehouseStore = mock(WarehouseStore.class);
        locationResolver = mock(LocationResolver.class);

        useCase = new ReplaceWarehouseUseCase(warehouseStore,
                locationResolver);
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
    void shouldReplaceWarehouseSuccessfully() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        10);

        Warehouse newWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        50,
                        10);

        Location location =
                new Location(
                        "ZWOLLE-001",
                        1,
                        100);

        when(locationResolver.resolveByIdentifier(
                "ZWOLLE-001"))
                .thenReturn(location);

        when(warehouseStore.getAll())
                .thenReturn(List.of(oldWarehouse));

        useCase.replace(
                oldWarehouse,
                newWarehouse);

        assertNotNull(
                oldWarehouse.archivedAt);

        assertNotNull(
                newWarehouse.createdAt);

        assertNull(
                newWarehouse.archivedAt);

        verify(warehouseStore)
                .update(oldWarehouse);

        verify(warehouseStore)
                .create(newWarehouse);
    }

    @Test
    void shouldRejectReplacementWithDifferentBusinessUnitCode() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        10);

        Warehouse newWarehouse =
                warehouse(
                        "MWH.002",
                        "ZWOLLE-001",
                        50,
                        10);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.replace(
                                oldWarehouse,
                                newWarehouse));

        verify(
                warehouseStore,
                never())
                .update(any());

        verify(
                warehouseStore,
                never())
                .create(any());
    }

    @Test
    void shouldRejectWhenNewCapacityCannotAccommodateOldStock() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        30);

        Warehouse newWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        20,
                        30);

        when(locationResolver.resolveByIdentifier(
                "ZWOLLE-001"))
                .thenReturn(
                        new Location(
                                "ZWOLLE-001",
                                1,
                                100));

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.replace(
                                oldWarehouse,
                                newWarehouse));

        verify(
                warehouseStore,
                never())
                .update(any());

        verify(
                warehouseStore,
                never())
                .create(any());
    }

    @Test
    void shouldRejectWhenStockDoesNotMatchOldWarehouse() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        30);

        Warehouse newWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        50,
                        20);

        when(locationResolver.resolveByIdentifier(
                "ZWOLLE-001"))
                .thenReturn(
                        new Location(
                                "ZWOLLE-001",
                                1,
                                100));

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.replace(
                                oldWarehouse,
                                newWarehouse));

        verify(
                warehouseStore,
                never())
                .update(any());

        verify(
                warehouseStore,
                never())
                .create(any());
    }

    @Test
    void shouldRejectInvalidReplacementLocation() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        10);

        Warehouse newWarehouse =
                warehouse(
                        "MWH.001",
                        "INVALID-001",
                        50,
                        10);

        when(locationResolver.resolveByIdentifier(
                "INVALID-001"))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.replace(
                                oldWarehouse,
                                newWarehouse));

        verify(
                warehouseStore,
                never())
                .update(any());

        verify(
                warehouseStore,
                never())
                .create(any());
    }

    @Test
    void shouldRejectNullReplacementWarehouse() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        10);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.replace(
                                oldWarehouse,
                                null));

        verify(
                warehouseStore,
                never())
                .update(any());

        verify(
                warehouseStore,
                never())
                .create(any());
    }

    @Test
    void shouldRejectAlreadyArchivedWarehouse() {

        Warehouse oldWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        40,
                        10);

        oldWarehouse.archivedAt =
                java.time.LocalDateTime.now();

        Warehouse newWarehouse =
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        50,
                        10);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        useCase.replace(
                                oldWarehouse,
                                newWarehouse));

        verify(
                warehouseStore,
                never())
                .update(any());

        verify(
                warehouseStore,
                never())
                .create(any());
    }
}