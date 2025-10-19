package com.pawnshop.pawnitemservice.controller;

import com.pawnshop.pawnitemservice.dto.PawnItemDTO;
import com.pawnshop.pawnitemservice.service.PawnItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class PawnItemController {
    private final PawnItemService pawnItemService;

    // Создание нового предмета
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PawnItemDTO> createPawnItem(@RequestBody PawnItemDTO pawnItemDTO) {
        return pawnItemService.createPawnItem(pawnItemDTO);
    }

    // Получение предмета по ID
    @GetMapping("/{id}")
    public Mono<PawnItemDTO> getPawnItemById(@PathVariable Long id) {
        return pawnItemService.getPawnItemById(id);
    }

    // Получение всех предметов клиента
    @GetMapping("/customer/{customerId}")
    public Flux<PawnItemDTO> getPawnItemsByCustomerId(@PathVariable Long customerId) {
        return pawnItemService.getPawnItemsByCustomerId(customerId);
    }

    // Обновление статуса предмета
    @PutMapping("/{id}/status")
    public Mono<PawnItemDTO> updatePawnItemStatus(@PathVariable Long id, @RequestParam String status) {
        return pawnItemService.updatePawnItemStatus(id, status);
    }
}
