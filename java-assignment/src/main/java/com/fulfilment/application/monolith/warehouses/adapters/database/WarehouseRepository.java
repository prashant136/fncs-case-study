package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.find("archivedAt is null").list().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse dbWarehouse = new DbWarehouse();

    dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;

    persist(dbWarehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    DbWarehouse dbWarehouse = find("businessUnitCode = ?1", warehouse.businessUnitCode).firstResult();

    if (dbWarehouse == null) {
      throw new IllegalArgumentException("Warehouse with business unit code " + warehouse.businessUnitCode + " does not exist.");
    }

    dbWarehouse.location = warehouse.location;
    dbWarehouse.capacity = warehouse.capacity;
    dbWarehouse.stock = warehouse.stock;
    dbWarehouse.createdAt = warehouse.createdAt;
    dbWarehouse.archivedAt = warehouse.archivedAt;
  }

  @Override
  public void remove(Warehouse warehouse) {
    DbWarehouse dbWarehouse = find("businessUnitCode = ?1", warehouse.businessUnitCode).firstResult();

    if (dbWarehouse != null) {
      delete(dbWarehouse);
    }
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse dbWarehouse = find("businessUnitCode = ?1", buCode).firstResult();
    return dbWarehouse == null ? null : dbWarehouse.toWarehouse();
  }
}