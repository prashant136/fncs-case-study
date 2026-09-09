package com.fulfilment.application.monolith.stores;

import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class StoreResourceTest {

    @Inject
    StoreResource storeResource;

    @InjectMock
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Test
    @TestTransaction
    void shouldGetAllStores() {
        Store store1 = new Store("TEST STORE A");
        store1.quantityProductsInStock = 10;

        Store store2 = new Store("TEST STORE B");
        store2.quantityProductsInStock = 20;

        store1.persist();
        store2.persist();

        List<Store> result = storeResource.get();

        assertNotNull(result);
        assertTrue(result.stream()
                .anyMatch(store -> "TEST STORE A".equals(store.name)));
        assertTrue(result.stream()
                .anyMatch(store -> "TEST STORE B".equals(store.name)));
    }

    @Test
    @TestTransaction
    void shouldGetStoreById() {
        Store store = new Store("TEST.GET");
        store.quantityProductsInStock = 10;
        store.persist();

        assertNotNull(store.id);

        Store result = storeResource.getSingle(store.id);

        assertNotNull(result);
        assertEquals("TEST.GET", result.name);
        assertEquals(10, result.quantityProductsInStock);
    }

    @Test
    @TestTransaction
    void shouldThrow404WhenStoreDoesNotExist() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.getSingle(999999L)
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );
    }

    @Test
    void shouldCreateStore() {
        Store store = new Store("TEST.CREATE");
        store.quantityProductsInStock = 25;

        var response = storeResource.create(store);

        assertEquals(201, response.getStatus());
        assertNotNull(store.id);

        verify(legacyStoreManagerGateway)
                .createStoreOnLegacySystem(store);
    }

    @Test
    @TestTransaction
    void shouldRejectCreateWhenIdIsAlreadySet() {
        Store store = new Store("TEST.INVALID.CREATE");
        store.id = 100L;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.create(store)
                );

        assertEquals(
                422,
                exception.getResponse().getStatus()
        );

        verify(
                legacyStoreManagerGateway,
                never()
        ).createStoreOnLegacySystem(any());
    }

    @Test
    @TestTransaction
    void shouldUpdateStore() {
        Store store = new Store("TEST.UPDATE");
        store.quantityProductsInStock = 10;
        store.persist();

        Store updatedStore = new Store("TEST.UPDATE.NEW");
        updatedStore.quantityProductsInStock = 50;

        Store result =
                storeResource.update(
                        store.id,
                        updatedStore
                );

        assertNotNull(result);
        assertEquals(
                "TEST.UPDATE.NEW",
                result.name
        );
        assertEquals(
                50,
                result.quantityProductsInStock
        );
    }

    @Test
    @TestTransaction
    void shouldRejectUpdateWhenNameIsNull() {
        Store updatedStore = new Store();

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.update(
                                999999L,
                                updatedStore
                        )
                );

        assertEquals(
                422,
                exception.getResponse().getStatus()
        );
    }

    @Test
    @TestTransaction
    void shouldThrow404WhenUpdatingUnknownStore() {
        Store updatedStore =
                new Store("TEST.UPDATE");

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.update(
                                999999L,
                                updatedStore
                        )
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );
    }

    @Test
    @TestTransaction
    void shouldPatchStore() {
        Store store = new Store("TEST.PATCH");
        store.quantityProductsInStock = 10;
        store.persist();

        Store updatedStore =
                new Store("TEST.PATCH.NEW");

        updatedStore.quantityProductsInStock = 30;

        Store result =
                storeResource.patch(
                        store.id,
                        updatedStore
                );

        assertNotNull(result);
        assertEquals(
                "TEST.PATCH.NEW",
                result.name
        );
        assertEquals(
                30,
                result.quantityProductsInStock
        );
    }

    @Test
    @TestTransaction
    void shouldRejectPatchWhenNameIsNull() {
        Store updatedStore = new Store();

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.patch(
                                999999L,
                                updatedStore
                        )
                );

        assertEquals(
                422,
                exception.getResponse().getStatus()
        );
    }

    @Test
    @TestTransaction
    void shouldThrow404WhenPatchingUnknownStore() {
        Store updatedStore =
                new Store("TEST.PATCH");

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.patch(
                                999999L,
                                updatedStore
                        )
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );
    }

    @Test
    @TestTransaction
    void shouldDeleteStore() {
        Store store = new Store("TEST.DELETE");
        store.persist();

        assertNotNull(store.id);

        var response =
                storeResource.delete(store.id);

        assertEquals(
                204,
                response.getStatus()
        );

        assertNull(
                Store.findById(store.id)
        );
    }

    @Test
    @TestTransaction
    void shouldThrow404WhenDeletingUnknownStore() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> storeResource.delete(999999L)
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );
    }

    @Test
    void shouldExecuteCreateLegacyCallAfterCommit() {

        Store store =
                new Store("TEST.AFTER.COMMIT");

        store.quantityProductsInStock = 10;

        QuarkusTransaction.requiringNew().run(() -> {

            storeResource.create(store);

            verify(
                    legacyStoreManagerGateway,
                    never()
            ).createStoreOnLegacySystem(any());
        });

        verify(
                legacyStoreManagerGateway
        ).createStoreOnLegacySystem(store);
    }

    @Test
    void shouldExecuteUpdateLegacyCallAfterCommit() {

        Store store =
                new Store("TEST.AFTER.UPDATE");

        store.quantityProductsInStock = 10;

        storeResource.create(store);

        reset(legacyStoreManagerGateway);

        Store updatedStore =
                new Store("TEST.AFTER.UPDATE.NEW");

        updatedStore.quantityProductsInStock = 20;

        QuarkusTransaction.requiringNew().run(() -> {

            storeResource.update(
                    store.id,
                    updatedStore
            );

            verify(
                    legacyStoreManagerGateway,
                    never()
            ).updateStoreOnLegacySystem(any());
        });

        verify(
                legacyStoreManagerGateway
        ).updateStoreOnLegacySystem(
                argThat(updated ->
                        "TEST.AFTER.UPDATE.NEW"
                                .equals(updated.name)
                                && updated.quantityProductsInStock == 20
                )
        );
    }

    @Test
    void shouldNotExecuteCreateLegacyCallWhenTransactionRollsBack() {

        Store store =
                new Store("TEST.ROLLBACK.CREATE");

        assertThrows(
                RuntimeException.class,
                () ->
                        QuarkusTransaction.requiringNew().run(() -> {

                            storeResource.create(store);

                            throw new RuntimeException(
                                    "Force rollback"
                            );
                        })
        );

        verify(
                legacyStoreManagerGateway,
                never()
        ).createStoreOnLegacySystem(any());
    }

    @Test
    void shouldNotExecuteUpdateLegacyCallWhenTransactionRollsBack() {

        Store store =
                new Store("TEST.ROLLBACK.UPDATE");

        storeResource.create(store);

        reset(legacyStoreManagerGateway);

        Store updatedStore =
                new Store("TEST.ROLLBACK.UPDATE.NEW");

        updatedStore.quantityProductsInStock = 20;

        assertThrows(
                RuntimeException.class,
                () ->
                        QuarkusTransaction.requiringNew().run(() -> {

                            storeResource.update(
                                    store.id,
                                    updatedStore
                            );

                            throw new RuntimeException(
                                    "Force rollback"
                            );
                        })
        );

        verify(
                legacyStoreManagerGateway,
                never()
        ).updateStoreOnLegacySystem(any());
    }

    @Test
    @TestTransaction
    void shouldHandleLegacyCreateFailure() {

        Store store =
                new Store("TEST.LEGACY.ERROR");

        doThrow(
                new RuntimeException("Legacy system failed")
        )
                .when(legacyStoreManagerGateway)
                .createStoreOnLegacySystem(any());

        assertDoesNotThrow(() ->
                QuarkusTransaction.requiringNew().run(() -> {
                    storeResource.create(store);
                })
        );

        verify(
                legacyStoreManagerGateway
        ).createStoreOnLegacySystem(store);
    }

    @Test
    @TestTransaction
    void shouldPatchStoreWithZeroStock() {

        Store store =
                new Store("TEST.PATCH.ZERO");

        store.quantityProductsInStock = 0;
        store.persist();

        Store updatedStore =
                new Store("TEST.PATCH.ZERO.NEW");

        updatedStore.quantityProductsInStock = 50;

        Store result =
                storeResource.patch(
                        store.id,
                        updatedStore
                );

        assertNotNull(result);

        assertEquals(
                "TEST.PATCH.ZERO.NEW",
                result.name
        );

        assertEquals(
                0,
                result.quantityProductsInStock
        );
    }
}