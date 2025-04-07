package com.microservices.order.web.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.microservices.order.AbstractIT;
import com.microservices.order.testdata.TestDataFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderControllerTest extends AbstractIT {

    @Nested
    class CreateOrderTest {
        @Test
        void shouldCreateOrderSuccessfully() {
            mockGetProductByCode("P100", "Product 1", new BigDecimal("25.50"));
            var payload = TestDataFactory.createValidOrderRequest();
            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(201)
                    .body("orderNumber", notNullValue());
        }

        @Test
        void shouldReturnBadRequestWhenProductIsInvalid() {
            mockGetProductByCode("P100", "Product 1", new BigDecimal("25.50"));
            var payload =
                    TestDataFactory.createOrderRequestWithCustomItem("ABCD", "Product 1", new BigDecimal("25.50"));
            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", is("Invalid Order Creation Request"))
                    .body("detail", is("Invalid product code: ABCD"))
                    .body("service", is("order-service"));
        }

        @Test
        void shouldReturnBadRequestWhenProductPriceIsNotMatching() {
            mockGetProductByCode("P100", "Product 1", new BigDecimal("25.50"));
            var payload = TestDataFactory.createOrderRequestWithCustomItem("P100", "Product 1", new BigDecimal("1000"));
            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(BAD_REQUEST.value())
                    .body("title", is("Invalid Order Creation Request"))
                    .body("detail", is("Product price not matching"))
                    .body("service", is("order-service"));
        }

        @Test
        void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
            var payload = TestDataFactory.createOrderRequestWithInvalidCustomer();
            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(BAD_REQUEST.value());
        }

        @Test
        void shouldReturnBadRequestForMalformedJson() {
            var payload =
                    """
                            {
                              "customer": {
                                "name": "John Doe",
                                "email": "john@example.com"
                                // missing closing brace!
                            """;

            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(BAD_REQUEST.value());
        }
    }
}
