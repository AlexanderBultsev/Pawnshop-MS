package com.pawnshop.loanservice.client;

import com.pawnshop.pawnitemservice.dto.PawnItemDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

// Реактивный FeignClient для взаимодействия с Pawn Item Service
@ReactiveFeignClient(name = "pawn-item-service")
public interface PawnItemClient {
    @GetMapping("/items/{id}")
    Mono<PawnItemDTO> getPawnItemById(@PathVariable("id") Long id);
}
