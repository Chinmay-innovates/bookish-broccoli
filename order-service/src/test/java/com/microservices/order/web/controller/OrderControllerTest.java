package com.microservices.order.web.controller;

import com.microservices.order.AbstractIT;
import com.microservices.order.testdata.TestDataFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
                    .statusCode(201)
                    .body("orderNumber", notNullValue());
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
