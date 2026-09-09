package com.fulfilment.application.monolith.products;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProductResourceTest {

    @Inject
    ProductResource productResource;

    @Inject
    ProductRepository productRepository;

    @Test
    @TestTransaction
    void shouldGetAllProducts() {
        Product product1 =
                new Product("TEST.PRODUCT.A");

        product1.description = "Product A";
        product1.price = new BigDecimal("100.00");
        product1.stock = 10;

        Product product2 =
                new Product("TEST.PRODUCT.B");

        product2.description = "Product B";
        product2.price = new BigDecimal("200.00");
        product2.stock = 20;

        productRepository.persist(product1);
        productRepository.persist(product2);

        List<Product> result =
                productResource.get();

        assertNotNull(result);

        assertTrue(
                result.stream()
                        .anyMatch(product ->
                                "TEST.PRODUCT.A"
                                        .equals(product.name))
        );

        assertTrue(
                result.stream()
                        .anyMatch(product ->
                                "TEST.PRODUCT.B"
                                        .equals(product.name))
        );
    }

    @Test
    @TestTransaction
    void shouldGetProductById() {
        Product product =
                new Product("TEST.GET.PRODUCT");

        product.description =
                "Test description";

        product.price =
                new BigDecimal("99.99");

        product.stock = 15;

        productRepository.persist(product);

        assertNotNull(product.id);

        Product result =
                productResource.getSingle(product.id);

        assertNotNull(result);

        assertEquals(
                "TEST.GET.PRODUCT",
                result.name
        );

        assertEquals(
                "Test description",
                result.description
        );

        assertEquals(
                new BigDecimal("99.99"),
                result.price
        );

        assertEquals(
                15,
                result.stock
        );
    }

    @Test
    void shouldThrow404WhenProductDoesNotExist() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.getSingle(999999L)
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        assertTrue(
                exception.getMessage()
                        .contains("does not exist")
        );
    }

    @Test
    void shouldCreateProduct() {
        Product product =
                new Product("TEST.CREATE.PRODUCT");

        product.description =
                "Created product";

        product.price =
                new BigDecimal("150.50");

        product.stock = 25;

        var response =
                productResource.create(product);

        assertEquals(
                201,
                response.getStatus()
        );

        assertNotNull(product.id);

        assertSame(
                product,
                response.getEntity()
        );
    }

    @Test
    @TestTransaction
    void shouldRejectCreateWhenIdIsAlreadySet() {
        Product product =
                new Product("TEST.INVALID.CREATE");

        product.id = 100L;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.create(product)
                );

        assertEquals(
                422,
                exception.getResponse().getStatus()
        );

        assertTrue(
                exception.getMessage()
                        .contains("Id was invalidly set")
        );
    }

    @Test
    @TestTransaction
    void shouldUpdateProduct() {
        Product product =
                new Product("TEST.UPDATE.PRODUCT");

        product.description =
                "Old description";

        product.price =
                new BigDecimal("100.00");

        product.stock = 10;

        productRepository.persist(product);

        Product updatedProduct =
                new Product("TEST.UPDATE.PRODUCT.NEW");

        updatedProduct.description =
                "Updated description";

        updatedProduct.price =
                new BigDecimal("250.00");

        updatedProduct.stock = 50;

        Product result =
                productResource.update(
                        product.id,
                        updatedProduct
                );

        assertNotNull(result);

        assertEquals(
                "TEST.UPDATE.PRODUCT.NEW",
                result.name
        );

        assertEquals(
                "Updated description",
                result.description
        );

        assertEquals(
                new BigDecimal("250.00"),
                result.price
        );

        assertEquals(
                50,
                result.stock
        );
    }

    @Test
    @TestTransaction
    void shouldRejectUpdateWhenProductNameIsNull() {
        Product updatedProduct =
                new Product();

        updatedProduct.name = null;

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.update(
                                999999L,
                                updatedProduct
                        )
                );

        assertEquals(
                422,
                exception.getResponse().getStatus()
        );

        assertTrue(
                exception.getMessage()
                        .contains("Product Name")
        );
    }

    @Test
    @TestTransaction
    void shouldThrow404WhenUpdatingUnknownProduct() {
        Product updatedProduct =
                new Product("TEST.UPDATE.UNKNOWN");

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.update(
                                999999L,
                                updatedProduct
                        )
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        assertTrue(
                exception.getMessage()
                        .contains("does not exist")
        );
    }

    @Test
    @TestTransaction
    void shouldDeleteProduct() {
        Product product =
                new Product("TEST.DELETE.PRODUCT");

        product.description =
                "Delete me";

        product.price =
                new BigDecimal("50.00");

        product.stock = 5;

        productRepository.persist(product);

        assertNotNull(product.id);

        var response =
                productResource.delete(product.id);

        assertEquals(
                204,
                response.getStatus()
        );

        assertNull(
                productRepository.findById(product.id)
        );
    }

    @Test
    @TestTransaction
    void shouldThrow404WhenDeletingUnknownProduct() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.delete(999999L)
                );

        assertEquals(
                404,
                exception.getResponse().getStatus()
        );

        assertTrue(
                exception.getMessage()
                        .contains("does not exist")
        );
    }
}