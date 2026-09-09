package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseTest {

    private WarehouseStore warehouseStore;
    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        warehouseStore = mock(WarehouseStore.class);

        useCase =
                new ArchiveWarehouseUseCase(
                        warehouseStore);
    }

    @Test
    void shouldArchiveWarehouse() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "MWH.001";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 40;
        warehouse.stock = 10;

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);

        verify(warehouseStore)
                .update(warehouse);
    }

    @Test
    void shouldRejectNullWarehouse() {

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.archive(null));

        verify(
                warehouseStore,
                never())
                .update(any());
    }

    @Test
    void shouldRejectAlreadyArchivedWarehouse() {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = "MWH.001";
        warehouse.archivedAt =
                LocalDateTime.now();

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.archive(warehouse));

        verify(
                warehouseStore,
                never())
                .update(any());
    }
}