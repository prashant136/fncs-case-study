package com.fulfilment.application.monolith.products;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductUsingNameConstructor() {

        Product product = new Product("TONSTAD");

        assertNotNull(product);
        assertEquals("TONSTAD", product.name);
    }

    @Test
    void shouldCreateProductUsingDefaultConstructor() {

        Product product = new Product();

        assertNotNull(product);
        assertNull(product.name);
        assertNull(product.description);
        assertNull(product.price);
        assertEquals(0, product.stock);
    }

    @Test
    void shouldSetProductFields() {

        Product product = new Product("KALLAX");

        product.description = "Shelf unit";
        product.price = new BigDecimal("499.99");
        product.stock = 25;

        assertEquals("KALLAX", product.name);
        assertEquals("Shelf unit", product.description);
        assertEquals(
                new BigDecimal("499.99"),
                product.price
        );
        assertEquals(25, product.stock);
    }
}