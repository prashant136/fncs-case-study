package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StoreResourceTransactionTest {

    private StoreResource storeResource;
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;
    private LegacyStoreManagerGateway legacyStoreManagerGateway;

    @BeforeEach
    void setUp() {
        storeResource = new StoreResource();

        transactionSynchronizationRegistry = mock(TransactionSynchronizationRegistry.class);

        legacyStoreManagerGateway = mock(LegacyStoreManagerGateway.class);

        // Inject mocks into StoreResource
        storeResource.transactionSynchronizationRegistry = transactionSynchronizationRegistry;

        storeResource.legacyStoreManagerGateway = legacyStoreManagerGateway;
    }

    @Test
    void shouldCallLegacySystemOnlyAfterSuccessfulCommit() {

        // Given
        Store store = new Store("TEST_STORE");

        // When
        storeResource.registerAfterCommit(() -> legacyStoreManagerGateway.createStoreOnLegacySystem(store));

        // Capture the synchronization callback registered with the transaction
        ArgumentCaptor<Synchronization> captor = ArgumentCaptor.forClass(Synchronization.class);

        verify(transactionSynchronizationRegistry).registerInterposedSynchronization(captor.capture());

        Synchronization synchronization = captor.getValue();

        // Before transaction completion, legacy system must NOT be called
        synchronization.beforeCompletion();

        verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any(Store.class));

        // Simulate successful database commit
        synchronization.afterCompletion(Status.STATUS_COMMITTED);

        // Then
        verify(legacyStoreManagerGateway).createStoreOnLegacySystem(store);
    }

    @Test
    void shouldNotCallLegacySystemWhenTransactionRollsBack() {

        // Given
        Store store = new Store("TEST_STORE");

        // When
        storeResource.registerAfterCommit(() -> legacyStoreManagerGateway.createStoreOnLegacySystem(store));

        ArgumentCaptor<Synchronization> captor = ArgumentCaptor.forClass(Synchronization.class);

        verify(transactionSynchronizationRegistry).registerInterposedSynchronization(captor.capture());

        Synchronization synchronization = captor.getValue();

        // Simulate database rollback
        synchronization.afterCompletion(Status.STATUS_ROLLEDBACK);

        // Then
        verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any(Store.class));
    }
}
