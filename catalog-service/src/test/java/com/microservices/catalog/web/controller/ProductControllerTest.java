package com.microservices.catalog.web.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.microservices.catalog.AbstractIT;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-data.sql")
class ProductControllerTest extends AbstractIT {

    @Test
    @DisplayName("Should return first page of products with pagination metadata")
    void shouldReturnFirstPageOfProducts() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("data", hasSize(10))
                .body("totalElements", is(15))
                .body("pageNumber", is(1))
                .body("totalPages", is(2))
                .body("isFirst", is(true))
                .body("isLast", is(false))
                .body("hasNext", is(true))
                .body("hasPrevious", is(false));
    }

    @Test
    @DisplayName("Should return second page of products with correct pagination flags")
    void shouldReturnSecondPageOfProducts() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products?page=2")
                .then()
                .statusCode(200)
                .body("data", hasSize(5))
                .body("totalElements", is(15))
                .body("pageNumber", is(2))
                .body("totalPages", is(2))
                .body("isFirst", is(false))
                .body("isLast", is(true))
                .body("hasNext", is(false))
                .body("hasPrevious", is(true));
    }

    @Test
    @DisplayName("Should return empty data for page 3")
    void shouldReturnEmptyPageWhenOutOfRange() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products?page=3")
                .then()
                .statusCode(200)
                .body("data", hasSize(0))
                .body("totalElements", is(15))
                .body("pageNumber", is(3))
                .body("totalPages", is(2))
                .body("isFirst", is(false))
                .body("isLast", is(true))
                .body("hasNext", is(false))
                .body("hasPrevious", is(true));
    }

    @Test
    @DisplayName("Should return first page when page number is negative")
    void shouldReturnFirstPageForNegativePageNumber() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products?page=-1")
                .then()
                .statusCode(200)
                .body("data", hasSize(10))
                .body("totalElements", is(15))
                .body("pageNumber", is(1))
                .body("totalPages", is(2))
                .body("isFirst", is(true))
                .body("isLast", is(false))
                .body("hasNext", is(true))
                .body("hasPrevious", is(false));
    }

    @Test
    @DisplayName("Should return products sorted by name ASC")
    void shouldSortByNameAsc() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products?sort=name,asc")
                .then()
                .statusCode(200)
                .body("data[0].name", is("A Game of Thrones"))
                .body("data[1].name", is("A Thousand Splendid Suns"))
                .body("data[2].name", is("Charlotte's Web"))
                .body("data[3].name", is("Fifty Shades of Grey"))
                .body("data[4].name", is("Gone with the Wind"))
                .body("data[5].name", is("One Flew Over the Cuckoo's Nest"))
                .body("data[6].name", is("The Alchemist"))
                .body("data[7].name", is("The Book Thief"));
    }
}
