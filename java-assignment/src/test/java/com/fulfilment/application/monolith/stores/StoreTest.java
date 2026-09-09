package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    @Test
    void shouldCreateStoreUsingNameConstructor() {

        Store store = new Store("TONSTAD");

        assertNotNull(store);
        assertEquals("TONSTAD", store.name);
        assertEquals(0, store.quantityProductsInStock);
    }


    @Test
    void shouldCreateStoreUsingDefaultConstructor() {

        Store store = new Store();

        assertNotNull(store);
        assertNull(store.name);
        assertEquals(0, store.quantityProductsInStock);
    }


    @Test
    void shouldSetStoreFields() {

        Store store = new Store("TEST_STORE");

        store.quantityProductsInStock = 25;

        assertEquals(
                "TEST_STORE",
                store.name
        );

        assertEquals(
                25,
                store.quantityProductsInStock
        );
    }
}