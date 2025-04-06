package com.microservices.order.domain.models;

import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    public String getLoginUserName() {
        return "user";
    }
}
