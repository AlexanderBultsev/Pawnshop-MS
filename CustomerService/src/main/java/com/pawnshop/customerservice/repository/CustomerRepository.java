package com.pawnshop.customerservice.repository;

import com.pawnshop.customerservice.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends R2dbcRepository<Customer, Long> {
    Mono<Customer> findByEmail(String email);
}
