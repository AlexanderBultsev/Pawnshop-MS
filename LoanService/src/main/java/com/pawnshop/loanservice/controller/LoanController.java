package com.pawnshop.loanservice.controller;

import com.pawnshop.loanservice.dto.LoanDTO;
import com.pawnshop.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    // Создание займа
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<LoanDTO> createLoan(@RequestBody LoanDTO loanDTO) {
        return loanService.createLoan(loanDTO);
    }

    // Получение займа по ID
    @GetMapping("/{id}")
    public Mono<LoanDTO> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id);
    }
}
