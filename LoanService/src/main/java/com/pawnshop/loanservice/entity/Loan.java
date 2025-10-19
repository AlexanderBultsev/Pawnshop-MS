package com.pawnshop.loanservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data // Lombok для геттеров/сеттеров
@Table("loans") // Таблица в базе данных
public class Loan {
    @Id
    private Long id; // Уникальный ID займа
    private Long customerId; // ID клиента
    private Long pawnItemId; // ID залогового предмета
    private Double amount; // Сумма займа
    private Double totalAmount; // Общая сумма с процентами
    private Double percentAmount; // Процент
    private String status; // Статус (ACTIVE, REPAID)
    private LocalDateTime dueDate; // Дата погашения
}
