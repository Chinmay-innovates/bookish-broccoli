package com.microservices.order.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import com.microservices.order.AbstractIT;
import io.restassured.http.ContentType;
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
                    	 "price": 25.50,
                    	 "quantity": 1
                        }
                      ]
                    }
                    """;
            given().contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(201)
                    .body("orderNumber", notNullValue());
        }
    }
}
