package com.pawnshop.loanservice.dto;

import com.pawnshop.loanservice.entity.Loan;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoanDTO {
    private Long id;
    private Long customerId;
    private Long pawnItemId;
    private Double amount;
    private Double totalAmount;
    private Double percentAmount;
    private String status;
    private LocalDateTime dueDate;

    public static LoanDTO fromEntity(Loan loan) {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(loan.getId());
        loanDTO.setCustomerId(loan.getCustomerId());
        loanDTO.setPawnItemId(loan.getPawnItemId());
        loanDTO.setAmount(loan.getAmount());
        loanDTO.setTotalAmount(loan.getTotalAmount());
        loanDTO.setPercentAmount(loan.getPercentAmount());
        loanDTO.setStatus(loan.getStatus());
        loanDTO.setDueDate(loan.getDueDate());
        return loanDTO;
    }
}
