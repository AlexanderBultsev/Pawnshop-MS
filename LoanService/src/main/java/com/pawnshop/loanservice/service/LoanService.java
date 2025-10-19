package com.pawnshop.loanservice.service;

import com.pawnshop.customerservice.dto.CustomerDTO;
import com.pawnshop.loanservice.client.CustomerClient;
import com.pawnshop.loanservice.client.PawnItemClient;
import com.pawnshop.loanservice.dto.LoanDTO;
import com.pawnshop.loanservice.entity.Loan;
import com.pawnshop.loanservice.repository.LoanRepository;
import com.pawnshop.pawnitemservice.dto.PawnItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final CustomerClient customerClient;
    private final PawnItemClient pawnItemClient;

    // Создание нового займа
    public Mono<LoanDTO> createLoan(LoanDTO loanDTO) {
        Mono<CustomerDTO> customerDTO = customerClient.getCustomerById(loanDTO.getCustomerId())
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
        Mono<PawnItemDTO> pawnItemDTO = pawnItemClient.getPawnItemById(loanDTO.getPawnItemId())
                .switchIfEmpty(Mono.error(new RuntimeException("Pawn item not found")));

        return Mono.zip(customerDTO, pawnItemDTO)
                .flatMap(tuple -> {
                    PawnItemDTO pawnItem = tuple.getT2();

                    // Проверка статуса предмета
                    if (!"IN_PLEDGE".equals(pawnItem.getStatus())) {
                        return Mono.error(new RuntimeException("Pawn item must be IN_PLEDGE"));
                    }

                    // Расчет суммы займа
                    Double loanAmount = pawnItem.getEstimatedValue();

                    // Расчет общей суммы с процентами (упрощенная формула)
                    Double totalAmount = loanAmount * (1 + loanDTO.getPercentAmount() * 30);

                    // Создаем сущность займа
                    Loan loan = new Loan();
                    loan.setCustomerId(loanDTO.getCustomerId());
                    loan.setPawnItemId(loanDTO.getPawnItemId());
                    loan.setAmount(loanAmount);
                    loan.setTotalAmount(totalAmount);
                    loan.setPercentAmount(loanDTO.getPercentAmount());
                    loan.setStatus("ACTIVE");
                    loan.setDueDate(LocalDateTime.now().plusDays(30)); // Фиксированный срок 30 дней

                    // Сохраняем в БД и возвращаем DTO
                    return loanRepository.save(loan)
                            .map(LoanDTO::fromEntity);
                });
    }

    // Получение займа по ID
    public Mono<LoanDTO> getLoanById(Long id) {
        return loanRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Loan not found")))
                .map(LoanDTO::fromEntity);
    }
}
