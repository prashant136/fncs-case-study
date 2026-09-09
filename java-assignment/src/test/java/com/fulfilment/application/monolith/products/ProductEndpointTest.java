package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductEndpointTest {

  @Test
  void shouldGetAllProducts() {
    given()
            .when()
            .get("/product")
            .then()
            .statusCode(200)
            .body(
                    containsString("TONSTAD"),
                    containsString("KALLAX"),
                    containsString("BESTÅ"));
  }

  @Test
  void shouldGetProductById() {
    Response createResponse = createProduct("GET-TEST-PRODUCT");

    Long id = createResponse.jsonPath().getLong("id");
    assertNotNull(id);

    given()
            .when()
            .get("/product/" + id)
            .then()
            .statusCode(200)
            .body("id", equalTo(id.intValue()))
            .body("name", equalTo("GET-TEST-PRODUCT"));
  }

  @Test
  void shouldReturn404WhenProductDoesNotExist() {
    given()
            .when()
            .get("/product/999999")
            .then()
            .statusCode(404)
            .body("code", equalTo(404));
  }

  @Test
  void shouldCreateProduct() {
    String product =
            """
            {
              "name": "CREATE-TEST-PRODUCT",
              "description": "Test product",
              "price": 99.99,
              "stock": 10
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post("/product")
            .then()
            .statusCode(201)
            .body("name", equalTo("CREATE-TEST-PRODUCT"))
            .body("description", equalTo("Test product"))
            .body("stock", equalTo(10));
  }

  @Test
  void shouldRejectProductWhenIdIsProvided() {
    String product =
            """
            {
              "id": 999999,
              "name": "INVALID-PRODUCT",
              "description": "Invalid product",
              "price": 50.00,
              "stock": 5
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post("/product")
            .then()
            .statusCode(422)
            .body("code", equalTo(422));
  }

  @Test
  void shouldUpdateProduct() {
    Response createResponse = createProduct("UPDATE-TEST-PRODUCT");

    Long id = createResponse.jsonPath().getLong("id");

    String updatedProduct =
            """
            {
              "name": "UPDATED-PRODUCT",
              "description": "Updated description",
              "price": 199.99,
              "stock": 25
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(updatedProduct)
            .when()
            .put("/product/" + id)
            .then()
            .statusCode(200)
            .body("id", equalTo(id.intValue()))
            .body("name", equalTo("UPDATED-PRODUCT"))
            .body("description", equalTo("Updated description"))
            .body("stock", equalTo(25));
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistingProduct() {
    String product =
            """
            {
              "name": "UPDATED-PRODUCT",
              "description": "Updated description",
              "price": 100.00,
              "stock": 10
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .put("/product/999999")
            .then()
            .statusCode(404)
            .body("code", equalTo(404));
  }

  @Test
  void shouldRejectUpdateWhenProductNameIsMissing() {
    Response createResponse = createProduct("NAME-VALIDATION-PRODUCT");

    Long id = createResponse.jsonPath().getLong("id");

    String product =
            """
            {
              "description": "Missing name",
              "price": 100.00,
              "stock": 10
            }
            """;

    given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .put("/product/" + id)
            .then()
            .statusCode(422)
            .body("code", equalTo(422));
  }

  @Test
  void shouldDeleteProduct() {
    Response createResponse = createProduct("DELETE-TEST-PRODUCT");

    Long id = createResponse.jsonPath().getLong("id");

    given()
            .when()
            .delete("/product/" + id)
            .then()
            .statusCode(204);

    given()
            .when()
            .get("/product/" + id)
            .then()
            .statusCode(404);
  }

  @Test
  void shouldReturn404WhenDeletingNonExistingProduct() {
    given()
            .when()
            .delete("/product/999999")
            .then()
            .statusCode(404)
            .body("code", equalTo(404));
  }

  private Response createProduct(String name) {
    String product =
            """
            {
              "name": "%s",
              "description": "Test product",
              "price": 99.99,
              "stock": 10
            }
            """.formatted(name);

    return given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post("/product")
            .then()
            .statusCode(201)
            .extract()
            .response();
  }
}