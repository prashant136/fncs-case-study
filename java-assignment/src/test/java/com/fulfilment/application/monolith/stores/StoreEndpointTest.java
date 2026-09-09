package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreEndpointTest {

    @Test
    void shouldGetAllStores() {
        given()
                .when()
                .get("/store")
                .then()
                .statusCode(200)
                .body(
                        containsString("TONSTAD"),
                        containsString("KALLAX"),
                        containsString("BESTÅ"));
    }

    @Test
    void shouldGetStoreById() {
        Response createResponse = createStore("GET-TEST-STORE");

        Long id = createResponse.jsonPath().getLong("id");
        assertNotNull(id);

        given()
                .when()
                .get("/store/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("GET-TEST-STORE"))
                .body("quantityProductsInStock", equalTo(20));
    }

    @Test
    void shouldReturn404WhenStoreDoesNotExist() {
        given()
                .when()
                .get("/store/999999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    void shouldCreateStore() {
        String store =
                """
                {
                  "name": "CREATE-TEST-STORE",
                  "quantityProductsInStock": 30
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(store)
                .when()
                .post("/store")
                .then()
                .statusCode(201)
                .body("name", equalTo("CREATE-TEST-STORE"))
                .body("quantityProductsInStock", equalTo(30));
    }

    @Test
    void shouldRejectStoreWhenIdIsProvided() {
        String store =
                """
                {
                  "id": 999999,
                  "name": "INVALID-STORE",
                  "quantityProductsInStock": 10
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(store)
                .when()
                .post("/store")
                .then()
                .statusCode(422)
                .body("code", equalTo(422));
    }

    @Test
    void shouldUpdateStore() {
        Response createResponse = createStore("UPDATE-TEST-STORE");

        Long id = createResponse.jsonPath().getLong("id");

        String updatedStore =
                """
                {
                  "name": "UPDATED-STORE",
                  "quantityProductsInStock": 50
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedStore)
                .when()
                .put("/store/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("UPDATED-STORE"))
                .body("quantityProductsInStock", equalTo(50));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingStore() {
        String store =
                """
                {
                  "name": "UPDATED-STORE",
                  "quantityProductsInStock": 50
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(store)
                .when()
                .put("/store/999999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    void shouldRejectUpdateWhenStoreNameIsMissing() {
        Response createResponse = createStore("VALIDATION-TEST-STORE");

        Long id = createResponse.jsonPath().getLong("id");

        String store =
                """
                {
                  "quantityProductsInStock": 50
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(store)
                .when()
                .put("/store/" + id)
                .then()
                .statusCode(422)
                .body("code", equalTo(422));
    }

    @Test
    void shouldPatchStore() {
        Response createResponse = createStore("PATCH-TEST-STORE");

        Long id = createResponse.jsonPath().getLong("id");

        String patch =
                """
                {
                  "name": "PATCHED-STORE",
                  "quantityProductsInStock": 75
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(patch)
                .when()
                .patch("/store/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("PATCHED-STORE"))
                .body("quantityProductsInStock", equalTo(75));
    }

    @Test
    void shouldReturn404WhenPatchingNonExistingStore() {
        String store =
                """
                {
                  "name": "PATCHED-STORE",
                  "quantityProductsInStock": 75
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(store)
                .when()
                .patch("/store/999999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    @Test
    void shouldRejectPatchWhenStoreNameIsMissing() {
        Response createResponse = createStore("PATCH-VALIDATION-STORE");

        Long id = createResponse.jsonPath().getLong("id");

        String patch =
                """
                {
                  "quantityProductsInStock": 100
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(patch)
                .when()
                .patch("/store/" + id)
                .then()
                .statusCode(422)
                .body("code", equalTo(422));
    }

    @Test
    void shouldDeleteStore() {
        Response createResponse = createStore("DELETE-TEST-STORE");

        Long id = createResponse.jsonPath().getLong("id");

        given()
                .when()
                .delete("/store/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/store/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingStore() {
        given()
                .when()
                .delete("/store/999999")
                .then()
                .statusCode(404)
                .body("code", equalTo(404));
    }

    private Response createStore(String name) {
        String store =
                """
                {
                  "name": "%s",
                  "quantityProductsInStock": 20
                }
                """.formatted(name);

        return given()
                .contentType(ContentType.JSON)
                .body(store)
                .when()
                .post("/store")
                .then()
                .statusCode(201)
                .extract()
                .response();
    }
}