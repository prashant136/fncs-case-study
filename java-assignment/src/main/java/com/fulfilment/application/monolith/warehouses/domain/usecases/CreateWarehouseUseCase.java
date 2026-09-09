package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    validateWarehouse(warehouse);

    warehouse.createdAt = LocalDateTime.now();
    warehouse.archivedAt = null;

    warehouseStore.create(warehouse);
  }

  private void validateWarehouse(Warehouse warehouse) {
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse cannot be null.");
    }

    if (isBlank(warehouse.businessUnitCode)) {
      throw new IllegalArgumentException("Business unit code is required.");
    }

    if (isBlank(warehouse.location)) {
      throw new IllegalArgumentException("Location is required.");
    }

    if (warehouse.capacity == null || warehouse.capacity <= 0) {
      throw new IllegalArgumentException("Warehouse capacity must be greater than zero.");
    }

    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new IllegalArgumentException("Warehouse stock cannot be negative.");
    }

    // Business Unit Code uniqueness
    Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);

    if (existing != null && existing.archivedAt == null) {
      throw new IllegalArgumentException("Warehouse with business unit code " + warehouse.businessUnitCode + " already exists.");
    }

    // Location validation
    Location location = locationResolver.resolveByIdentifier(warehouse.location);

    if (location == null) {
      throw new IllegalArgumentException("Location " + warehouse.location + " does not exist.");
    }

    // Maximum number of warehouses
    List<Warehouse> warehouses = warehouseStore.getAll().stream()
                    .filter(w -> warehouse.location.equals(w.location))
                    .filter(w -> w.archivedAt == null)
                    .toList();

    if (warehouses.size() >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException("Maximum number of warehouses reached for location " + warehouse.location);
    }

    // Maximum total capacity
    int existingCapacity = warehouses.stream()
                    .filter(w -> w.capacity != null)
                    .mapToInt(w -> w.capacity)
                    .sum();

    if (existingCapacity + warehouse.capacity > location.maxCapacity) {

      throw new IllegalArgumentException("Warehouse capacity exceeds maximum capacity " + "allowed for location " + warehouse.location);
    }

    // Stock must fit warehouse capacity
    if (warehouse.stock > warehouse.capacity) {
      throw new IllegalArgumentException("Warehouse stock cannot exceed warehouse capacity.");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}