package com.microservices.order.web.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.microservices.order.AbstractIT;
import com.microservices.order.testdata.TestDataFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderControllerTest extends AbstractIT {

    @Nested
    class CreateOrderTest {
        @Test
        void shouldCreateOrderSuccessfully() {
            var payload =
                    """
                            {
                              "customer": {
                                "name": "John Doe",
                                "email": "john@example.com",
                                "phone": "123-456-7890"
                              },
                              "deliveryAddress": {
                                "addressLine1": "123 Main St",
                                "addressLine2": "Apt 4B",
                                "city": "Springfield",
                                "state": "IL",
                                "zipCode": "62704",
                                "country": "USA"
                              },
                            	  "items": [
                                {
                                 "code":"P100",
                            	 "name":"Product 1",
                            	 "price": 34,
                            	 "quantity": 1
                                }
                              ]
                            }
                            """;
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
            var payload =
                    """
                            {
                              "customer": {
                                "name": "John Doe",
                                "email": "john@example.com",
                                "phone": "123-456-7890"
                              },
                              "deliveryAddress": {
                                "addressLine1": "123 Main St",
                                "addressLine2": "Apt 4B",
                                "city": "Springfield",
                                "state": "IL",
                                "zipCode": "62704",
                                "country": "USA"
                              },
                            	  "items": [
                                {
                                 "code":"ABCD",
                            	 "name":"Product 1",
                            	 "price": 34,
                            	 "quantity": 1
                                }
                              ]
                            }
                            """;
            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(400)
                    .body("title", is("Invalid Order Creation Request"))
                    .body("detail", is("Invalid product code: ABCD"))
                    .body("service", is("order-service"));
        }

        @Test
        void shouldReturnBadRequestWhenProductPriceIsNotMatching() {
            var payload =
                    """
                            {
                              "customer": {
                                "name": "John Doe",
                                "email": "john@example.com",
                                "phone": "123-456-7890"
                              },
                              "deliveryAddress": {
                                "addressLine1": "123 Main St",
                                "addressLine2": "Apt 4B",
                                "city": "Springfield",
                                "state": "IL",
                                "zipCode": "62704",
                                "country": "USA"
                              },
                            	  "items": [
                                {
                                 "code":"P100",
                            	 "name":"Product 1",
                            	 "price": 25.50,
                            	 "quantity": 1
                                }
                              ]
                            }
                            """;
            given().contentType(JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(400)
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
