package com.pawnshop.loanservice.client;

import com.pawnshop.customerservice.dto.CustomerDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

// Реактивный FeignClient для взаимодействия с Customer Service
@ReactiveFeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/customers/{id}")
    Mono<CustomerDTO> getCustomerById(@PathVariable("id") Long id);
}
