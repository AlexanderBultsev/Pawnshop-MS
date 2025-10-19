package com.pawnshop.pawnitemservice.repository;

import com.pawnshop.pawnitemservice.entity.PawnItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface PawnItemRepository extends R2dbcRepository<PawnItem, Long> {
    // Реактивный поиск предметов по ID клиента
    Flux<PawnItem> findByCustomerId(Long customerId);
}
