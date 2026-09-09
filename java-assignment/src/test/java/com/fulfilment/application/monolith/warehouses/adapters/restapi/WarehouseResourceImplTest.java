package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WarehouseResourceImplTest {

    @InjectMocks
    private WarehouseResourceImpl resource;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private CreateWarehouseOperation createWarehouseOperation;

    @Mock
    private ArchiveWarehouseOperation archiveWarehouseOperation;

    @Mock
    private ReplaceWarehouseOperation replaceWarehouseOperation;

    @Test
    void shouldListAllWarehouses() {

        Warehouse warehouse1 = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        Warehouse warehouse2 = createDomainWarehouse(
                "MWH.TEST.002",
                "AMSTERDAM-001",
                50,
                5
        );

        when(warehouseRepository.getAll())
                .thenReturn(List.of(warehouse1, warehouse2));

        List<com.warehouse.api.beans.Warehouse> result =
                resource.listAllWarehousesUnits();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                "MWH.TEST.001",
                result.get(0).getBusinessUnitCode()
        );

        assertEquals(
                "ZWOLLE-001",
                result.get(0).getLocation()
        );

        assertEquals(
                100,
                result.get(0).getCapacity()
        );

        assertEquals(
                10,
                result.get(0).getStock()
        );

        assertEquals(
                "MWH.TEST.002",
                result.get(1).getBusinessUnitCode()
        );

        verify(warehouseRepository).getAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoWarehousesExist() {

        when(warehouseRepository.getAll())
                .thenReturn(List.of());

        List<com.warehouse.api.beans.Warehouse> result =
                resource.listAllWarehousesUnits();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(warehouseRepository).getAll();
    }

    @Test
    void shouldCreateWarehouse() {

        com.warehouse.api.beans.Warehouse data =
                createApiWarehouse(
                        "MWH.TEST.001",
                        "ZWOLLE-001",
                        100,
                        10
                );

        doAnswer(invocation -> {

            Warehouse warehouse = invocation.getArgument(0);

            assertEquals(
                    "MWH.TEST.001",
                    warehouse.businessUnitCode
            );

            assertEquals(
                    "ZWOLLE-001",
                    warehouse.location
            );

            assertEquals(
                    100,
                    warehouse.capacity
            );

            assertEquals(
                    10,
                    warehouse.stock
            );

            return null;

        }).when(createWarehouseOperation)
                .create(any(Warehouse.class));

        com.warehouse.api.beans.Warehouse result =
                resource.createANewWarehouseUnit(data);

        assertNotNull(result);

        assertEquals(
                "MWH.TEST.001",
                result.getBusinessUnitCode()
        );

        assertEquals(
                "ZWOLLE-001",
                result.getLocation()
        );

        assertEquals(
                100,
                result.getCapacity()
        );

        assertEquals(
                10,
                result.getStock()
        );

        verify(createWarehouseOperation)
                .create(any(Warehouse.class));
    }

    @Test
    void shouldGetWarehouseByBusinessUnitCode() {

        Warehouse warehouse = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "MWH.TEST.001"
                )
        ).thenReturn(warehouse);

        com.warehouse.api.beans.Warehouse result =
                resource.getAWarehouseUnitByID(
                        "MWH.TEST.001"
                );

        assertNotNull(result);

        assertEquals(
                "MWH.TEST.001",
                result.getBusinessUnitCode()
        );

        assertEquals(
                "ZWOLLE-001",
                result.getLocation()
        );

        assertEquals(
                100,
                result.getCapacity()
        );

        assertEquals(
                10,
                result.getStock()
        );

        verify(warehouseRepository)
                .findByBusinessUnitCode("MWH.TEST.001");
    }

    @Test
    void shouldThrow404WhenWarehouseDoesNotExist() {

        when(
                warehouseRepository.findByBusinessUnitCode("UNKNOWN")
        ).thenReturn(null);

        var exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> resource.getAWarehouseUnitByID("UNKNOWN")
        );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        verify(warehouseRepository)
                .findByBusinessUnitCode("UNKNOWN");
    }

    @Test
    void shouldThrow404WhenWarehouseIsArchived() {

        Warehouse warehouse = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        warehouse.archivedAt = LocalDateTime.now();

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "MWH.TEST.001"
                )
        ).thenReturn(warehouse);

        var exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> resource.getAWarehouseUnitByID(
                        "MWH.TEST.001"
                )
        );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );
    }

    @Test
    void shouldArchiveWarehouse() {

        Warehouse warehouse = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "MWH.TEST.001"
                )
        ).thenReturn(warehouse);

        resource.archiveAWarehouseUnitByID(
                "MWH.TEST.001"
        );

        verify(archiveWarehouseOperation)
                .archive(warehouse);
    }

    @Test
    void shouldNotArchiveUnknownWarehouse() {

        when(
                warehouseRepository.findByBusinessUnitCode("UNKNOWN")
        ).thenReturn(null);

        var exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> resource.archiveAWarehouseUnitByID(
                        "UNKNOWN"
                )
        );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        verify(
                archiveWarehouseOperation,
                never()
        ).archive(any(Warehouse.class));
    }

    @Test
    void shouldNotArchiveAlreadyArchivedWarehouse() {

        Warehouse warehouse = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        warehouse.archivedAt = LocalDateTime.now();

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "MWH.TEST.001"
                )
        ).thenReturn(warehouse);

        var exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> resource.archiveAWarehouseUnitByID(
                        "MWH.TEST.001"
                )
        );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        verify(
                archiveWarehouseOperation,
                never()
        ).archive(any(Warehouse.class));
    }

    @Test
    void shouldReplaceWarehouse() {

        Warehouse currentWarehouse = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        com.warehouse.api.beans.Warehouse data =
                createApiWarehouse(
                        "MWH.TEST.001",
                        "ZWOLLE-001",
                        150,
                        10
                );

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "MWH.TEST.001"
                )
        ).thenReturn(currentWarehouse);

        com.warehouse.api.beans.Warehouse result =
                resource.replaceTheCurrentActiveWarehouse(
                        "MWH.TEST.001",
                        data
                );

        assertNotNull(result);

        assertEquals(
                "MWH.TEST.001",
                result.getBusinessUnitCode()
        );

        assertEquals(
                "ZWOLLE-001",
                result.getLocation()
        );

        assertEquals(
                150,
                result.getCapacity()
        );

        assertEquals(
                10,
                result.getStock()
        );

        verify(replaceWarehouseOperation)
                .replace(
                        eq(currentWarehouse),
                        any(Warehouse.class)
                );
    }

    @Test
    void shouldNotReplaceUnknownWarehouse() {

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "UNKNOWN"
                )
        ).thenReturn(null);

        com.warehouse.api.beans.Warehouse data =
                createApiWarehouse(
                        "UNKNOWN",
                        "ZWOLLE-001",
                        100,
                        10
                );

        var exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> resource.replaceTheCurrentActiveWarehouse(
                        "UNKNOWN",
                        data
                )
        );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        verify(
                replaceWarehouseOperation,
                never()
        ).replace(
                any(Warehouse.class),
                any(Warehouse.class)
        );
    }

    @Test
    void shouldNotReplaceArchivedWarehouse() {

        Warehouse currentWarehouse = createDomainWarehouse(
                "MWH.TEST.001",
                "ZWOLLE-001",
                100,
                10
        );

        currentWarehouse.archivedAt = LocalDateTime.now();

        when(
                warehouseRepository.findByBusinessUnitCode(
                        "MWH.TEST.001"
                )
        ).thenReturn(currentWarehouse);

        com.warehouse.api.beans.Warehouse data =
                createApiWarehouse(
                        "MWH.TEST.001",
                        "ZWOLLE-001",
                        150,
                        10
                );

        var exception = assertThrows(
                jakarta.ws.rs.WebApplicationException.class,
                () -> resource.replaceTheCurrentActiveWarehouse(
                        "MWH.TEST.001",
                        data
                )
        );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        verify(
                replaceWarehouseOperation,
                never()
        ).replace(
                any(Warehouse.class),
                any(Warehouse.class)
        );
    }

    private Warehouse createDomainWarehouse(
            String businessUnitCode,
            String location,
            int capacity,
            int stock) {

        Warehouse warehouse = new Warehouse();

        warehouse.businessUnitCode = businessUnitCode;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;
        warehouse.createdAt = LocalDateTime.now();
        warehouse.archivedAt = null;

        return warehouse;
    }

    private com.warehouse.api.beans.Warehouse createApiWarehouse(
            String businessUnitCode,
            String location,
            int capacity,
            int stock) {

        com.warehouse.api.beans.Warehouse warehouse =
                new com.warehouse.api.beans.Warehouse();

        warehouse.setBusinessUnitCode(businessUnitCode);
        warehouse.setLocation(location);
        warehouse.setCapacity(capacity);
        warehouse.setStock(stock);

        return warehouse;
    }
}