package com.pawnshop.loanservice.repository;

import com.pawnshop.loanservice.entity.Loan;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanRepository extends R2dbcRepository<Loan, Long> {
    Flux<Loan> findByCustomerId(Long customerId);
    Mono<Loan> findByPawnItemId(Long pawnItemId);
}
