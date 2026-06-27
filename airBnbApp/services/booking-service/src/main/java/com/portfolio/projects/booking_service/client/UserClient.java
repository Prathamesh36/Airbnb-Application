package com.portfolio.projects.booking_service.client;

import com.portfolio.projects.booking_service.client.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8081}")
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable Long id);
}
