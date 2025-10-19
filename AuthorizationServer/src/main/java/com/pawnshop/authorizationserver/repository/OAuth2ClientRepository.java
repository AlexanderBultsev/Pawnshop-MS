package com.pawnshop.authorizationserver.repository;

import com.pawnshop.authorizationserver.entity.OAuth2Client;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OAuth2ClientRepository extends R2dbcRepository<OAuth2Client, String> {
    Mono<OAuth2Client> findByClientId(String clientId);
}
