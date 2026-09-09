package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

class LocationGatewayTest {

  private final LocationGateway gateway =
          new LocationGateway();

  @Test
  void shouldFindExistingLocation() {

    Location location =
            gateway.resolveByIdentifier(
                    "ZWOLLE-001");

    assertNotNull(location);
    assertEquals(
            "ZWOLLE-001",
            location.identification);
    assertEquals(
            1,
            location.maxNumberOfWarehouses);
    assertEquals(
            40,
            location.maxCapacity);
  }

  @Test
  void shouldFindAmsterdamLocation() {

    Location location = gateway.resolveByIdentifier("AMSTERDAM-001");

    assertNotNull(location);

    assertEquals(
            "AMSTERDAM-001",
            location.identification);
  }

  @Test
  void shouldReturnNullForUnknownLocation() {

    Location location =
            gateway.resolveByIdentifier(
                    "UNKNOWN-001");

    assertNull(location);
  }
}