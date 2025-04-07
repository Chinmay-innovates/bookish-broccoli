package com.microservices.order.web.controller;

import static com.microservices.order.testdata.TestDataFactory.*;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.order.domain.models.CreateOrderRequest;
import com.microservices.order.domain.models.OrderService;
import com.microservices.order.domain.models.SecurityService;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerUnitTests {
    @MockitoBean
    @Autowired
    private OrderService orderService;

    @MockitoBean
    @Autowired
    private SecurityService securityService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static Stream<Arguments> createOrderRequestProvider() {
        return Stream.of(
                arguments(named("Order with Invalid Customer", createOrderRequestWithInvalidCustomer())),
                arguments(named("Order with Invalid Delivery Address", createOrderRequestWithInvalidDeliveryAddress())),
                arguments(named("Order with No Items", createOrderRequestWithNoItems())));
    }

    @BeforeEach
    void setUp() {
        given(securityService.getLoginUserName()).willReturn("visper");
    }

    @ParameterizedTest(name = "[{index}]-{0}")
    @MethodSource("createOrderRequestProvider")
    void shouldReturnBadRequestWhenOrderPayloadIsInvalid(CreateOrderRequest request) throws Exception {
        given(orderService.createOrder(eq("visper"), any(CreateOrderRequest.class)))
                .willReturn(null);

        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
