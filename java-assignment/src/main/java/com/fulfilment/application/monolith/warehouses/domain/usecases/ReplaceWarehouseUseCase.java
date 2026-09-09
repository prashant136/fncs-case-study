package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse currentWarehouse, Warehouse newWarehouse) {

    if (currentWarehouse == null) {
      throw new IllegalArgumentException("Warehouse to replace does not exist.");
    }

    if (currentWarehouse.archivedAt != null) {
      throw new IllegalArgumentException("Warehouse is already archived.");
    }

    validateReplacement(currentWarehouse, newWarehouse);

    // Archive old warehouse
    currentWarehouse.archivedAt = LocalDateTime.now();

    warehouseStore.update(currentWarehouse);

    // Create replacement
    newWarehouse.createdAt = LocalDateTime.now();

    newWarehouse.archivedAt = null;

    warehouseStore.create(newWarehouse);
  }

  private void validateReplacement(Warehouse oldWarehouse, Warehouse newWarehouse) {

    if (newWarehouse == null) {
      throw new IllegalArgumentException("Replacement warehouse cannot be null.");
    }

    if (newWarehouse.businessUnitCode == null || newWarehouse.businessUnitCode.isBlank()) {

      throw new IllegalArgumentException(
              "Business unit code is required.");
    }

    if (newWarehouse.location == null || newWarehouse.location.isBlank()) {

      throw new IllegalArgumentException("Location is required.");
    }

    if (newWarehouse.capacity == null || newWarehouse.capacity <= 0) {

      throw new IllegalArgumentException(
              "Warehouse capacity must be greater than zero.");
    }

    if (newWarehouse.stock == null || newWarehouse.stock < 0) {

      throw new IllegalArgumentException(
              "Warehouse stock cannot be negative.");
    }

    // BU code must be the same during replacement
    if (!oldWarehouse.businessUnitCode.equals(newWarehouse.businessUnitCode)) {

      throw new IllegalArgumentException(
              "Replacement warehouse must use the same "
                      + "business unit code.");
    }

    // Location must be valid
    Location location = locationResolver.resolveByIdentifier(newWarehouse.location);

    if (location == null) {
      throw new IllegalArgumentException("Location " + newWarehouse.location + " does not exist.");
    }

    // New warehouse must accommodate old stock
    if (newWarehouse.capacity < oldWarehouse.stock) {

      throw new IllegalArgumentException("Replacement warehouse capacity cannot " + "accommodate existing stock.");
    }

    // Stock must remain exactly the same
    if (!newWarehouse.stock.equals(oldWarehouse.stock)) {

      throw new IllegalArgumentException("Replacement warehouse stock must match " + "the current warehouse stock.");
    }

    // Stock cannot exceed new capacity
    if (newWarehouse.stock > newWarehouse.capacity) {
      throw new IllegalArgumentException("Warehouse stock cannot exceed capacity.");
    }

    /*
     * When replacing in the same location, the old
     * warehouse is going away first. Therefore its
     * capacity should not be counted against the
     * new warehouse.
     */
    List<Warehouse> otherWarehouses = warehouseStore.getAll().stream()
                    .filter(w -> w.archivedAt == null
                                    && !w.businessUnitCode.equals(
                                    oldWarehouse.businessUnitCode))
                    .filter(w ->
                            newWarehouse.location.equals(
                                    w.location))
                    .toList();

    if (otherWarehouses.size() >= location.maxNumberOfWarehouses) {
      throw new IllegalArgumentException("Maximum number of warehouses reached " + "for location " + newWarehouse.location);
    }

    int otherCapacity = otherWarehouses.stream()
                    .filter(w -> w.capacity != null)
                    .mapToInt(w -> w.capacity)
                    .sum();

    if (otherCapacity + newWarehouse.capacity > location.maxCapacity) {
      throw new IllegalArgumentException("Replacement warehouse capacity exceeds " + "location maximum capacity.");
    }
  }
}