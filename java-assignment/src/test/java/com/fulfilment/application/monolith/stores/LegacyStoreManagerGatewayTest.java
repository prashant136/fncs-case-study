package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LegacyStoreManagerGatewayTest {

    @Test
    void shouldCreateStoreOnLegacySystem() {

        LegacyStoreManagerGateway gateway =
                new LegacyStoreManagerGateway();

        Store store = new Store("TEST_CREATE_STORE");

        store.quantityProductsInStock = 10;

        assertDoesNotThrow(
                () -> gateway.createStoreOnLegacySystem(store)
        );
    }


    @Test
    void shouldUpdateStoreOnLegacySystem() {

        LegacyStoreManagerGateway gateway =
                new LegacyStoreManagerGateway();

        Store store = new Store("TEST_UPDATE_STORE");

        store.quantityProductsInStock = 20;

        assertDoesNotThrow(
                () -> gateway.updateStoreOnLegacySystem(store)
        );
    }
}