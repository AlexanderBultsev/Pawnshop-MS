package com.pawnshop.authorizationserver.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("oauth2_clients")
public class OAuth2Client {
    @Id
    private String clientId; // ID клиента
    private String clientSecret; // Секрет клиента
    private String redirectUris; // URI для редиректа
    private String scopes; // Разрешенные скопы
    private String grantTypes; // Разрешенные типы грантов
}
