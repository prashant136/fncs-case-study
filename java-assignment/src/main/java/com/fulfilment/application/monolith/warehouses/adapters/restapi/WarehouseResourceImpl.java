package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.warehouse.api.WarehouseResource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  private WarehouseRepository warehouseRepository;

  @Inject
  private CreateWarehouseOperation createWarehouseOperation;

  @Inject
  private ArchiveWarehouseOperation archiveWarehouseOperation;

  @Inject
  private ReplaceWarehouseOperation replaceWarehouseOperation;

  @Override
  public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll()
            .stream()
            .map(this::toWarehouseResponse)
            .toList();
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse
  createANewWarehouseUnit(@NotNull com.warehouse.api.beans.Warehouse data) {
    Warehouse warehouse = toDomain(data);
    createWarehouseOperation.create(warehouse);
    return toWarehouseResponse(warehouse);
  }

  @Override
  public com.warehouse.api.beans.Warehouse
  getAWarehouseUnitByID(String id) {

    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null || warehouse.archivedAt != null) {

      throw new WebApplicationException("Warehouse with business unit code " + id + " does not exist.", Response.Status.NOT_FOUND);
    }

    return toWarehouseResponse(warehouse);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {

    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null || warehouse.archivedAt != null) {

      throw new WebApplicationException("Warehouse with business unit code " + id + " does not exist.", Response.Status.NOT_FOUND);
    }

    archiveWarehouseOperation.archive(warehouse);
  }

  @Override
  @Transactional
  public com.warehouse.api.beans.Warehouse
  replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull com.warehouse.api.beans.Warehouse data) {

    Warehouse currentWarehouse = warehouseRepository.findByBusinessUnitCode(businessUnitCode);

    if (currentWarehouse == null || currentWarehouse.archivedAt != null) {

      throw new WebApplicationException(
              "Warehouse with business unit code "
                      + businessUnitCode
                      + " does not exist.",
              Response.Status.NOT_FOUND);
    }

    Warehouse newWarehouse = toDomain(data);

    replaceWarehouseOperation.replace(
            currentWarehouse,
            newWarehouse);

    return toWarehouseResponse(newWarehouse);
  }

  private Warehouse toDomain(
          com.warehouse.api.beans.Warehouse data) {

    Warehouse warehouse = new Warehouse();

    warehouse.businessUnitCode =
            data.getBusinessUnitCode();

    warehouse.location =
            data.getLocation();

    warehouse.capacity =
            data.getCapacity();

    warehouse.stock =
            data.getStock();

    return warehouse;
  }

  private com.warehouse.api.beans.Warehouse
  toWarehouseResponse(Warehouse warehouse) {

    var response =
            new com.warehouse.api.beans.Warehouse();

    response.setBusinessUnitCode(
            warehouse.businessUnitCode);

    response.setLocation(
            warehouse.location);

    response.setCapacity(
            warehouse.capacity);

    response.setStock(
            warehouse.stock);

    return response;
  }
}