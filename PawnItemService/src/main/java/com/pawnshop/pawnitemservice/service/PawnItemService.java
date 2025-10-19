package com.pawnshop.pawnitemservice.service;

import com.pawnshop.pawnitemservice.client.CustomerClient;
import com.pawnshop.pawnitemservice.dto.PawnItemDTO;
import com.pawnshop.pawnitemservice.entity.PawnItem;
import com.pawnshop.pawnitemservice.repository.PawnItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PawnItemService {
    private final PawnItemRepository pawnItemRepository;
    private final CustomerClient customerClient;

    // Создание нового залогового предмета
    public Mono<PawnItemDTO> createPawnItem(PawnItemDTO pawnItemDTO) {
        // Проверка существования клиента через Customer Service
        return customerClient.getCustomerById(pawnItemDTO.getCustomerId())
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")))
                .flatMap(customer -> {
                    PawnItem pawnItem = new PawnItem();
                    pawnItem.setCustomerId(pawnItemDTO.getCustomerId());
                    pawnItem.setDescription(pawnItemDTO.getDescription());
                    pawnItem.setCategory(pawnItemDTO.getCategory());
                    pawnItem.setEstimatedValue(pawnItemDTO.getEstimatedValue());
                    pawnItem.setStatus("IN_PLEDGE");

                    return pawnItemRepository.save(pawnItem)
                            .map(PawnItemDTO::fromEntity);
                });
    }

    // Получение предмета по ID
    public Mono<PawnItemDTO> getPawnItemById(Long id) {
        return pawnItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pawn item not found")))
                .map(PawnItemDTO::fromEntity);
    }

    // Получение всех предметов клиента
    public Flux<PawnItemDTO> getPawnItemsByCustomerId(Long customerId) {
        return pawnItemRepository.findByCustomerId(customerId)
                .map(PawnItemDTO::fromEntity);
    }

    // Обновление статуса предмета
    public Mono<PawnItemDTO> updatePawnItemStatus(Long id, String status) {
        return pawnItemRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Pawn item not found")))
                .flatMap(item -> {
                    item.setStatus(status); // Обновляем статус
                    return pawnItemRepository.save(item)
                            .map(PawnItemDTO::fromEntity);
                });
    }
}
