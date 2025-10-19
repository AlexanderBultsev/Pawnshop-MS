package com.pawnshop.authorizationserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("users")
public class User {
    @Id
    private Long id; // Уникальный идентификатор
    private String username; // Имя пользователя
    private String password; // Хэш пароля
    private String roles; // Роли, разделенные запятыми (например, USER,ADMIN)
}
