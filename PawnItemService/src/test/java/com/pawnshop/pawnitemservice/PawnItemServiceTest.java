package com.pawnshop.pawnitemservice;

import com.pawnshop.customerservice.dto.CustomerDTO;
import com.pawnshop.pawnitemservice.client.CustomerClient;
import com.pawnshop.pawnitemservice.dto.PawnItemDTO;
import com.pawnshop.pawnitemservice.service.PawnItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = PawnItemServiceApplication.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false", // Отключаем Spring Cloud Config
        "eureka.client.enabled=false", // Отключаем Eureka Client
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", // H2 для тестов
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password=",
        "spring.sql.init.mode=always" // Включаем инициализацию SQL-скриптов
})
class PawnItemServiceTest {

    @Autowired
    private PawnItemService pawnItemService;

    @Autowired
    private DatabaseClient databaseClient;

    @MockBean
    private CustomerClient customerClient;

    @BeforeEach
    void setUp() {
        // Очищаем таблицу pawn_items перед каждым тестом
        databaseClient.sql("TRUNCATE TABLE pawn_items")
                .then()
                .subscribe();
    }

    @Test
    void testCreatePawnItem() {
        // Подготовка тестовых данных
        PawnItemDTO pawnItemDTO = new PawnItemDTO();
        pawnItemDTO.setCustomerId(1L);
        pawnItemDTO.setDescription("Sink");
        pawnItemDTO.setCategory("Gold");
        pawnItemDTO.setEstimatedValue(1111.11);

        CustomerDTO customerDTO = new CustomerDTO();

        when(customerClient.getCustomerById(1L)).thenReturn(Mono.just(customerDTO));

        // Тестирование
        Mono<PawnItemDTO> result = pawnItemService.createPawnItem(pawnItemDTO);

        // Проверка результата
        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getId() != null && // ID должен быть сгенерирован
                                dto.getCustomerId().equals(1L) &&
                                dto.getDescription().equals("Sink") &&
                                dto.getCategory().equals("Gold") &&
                                dto.getEstimatedValue().equals((1111.11)))
                .verifyComplete();
    }

    @Test
    void testGetPawnItemById() {
        PawnItemDTO pawnItemDTO = new PawnItemDTO();
        pawnItemDTO.setCustomerId(1L);
        pawnItemDTO.setDescription("Sink");
        pawnItemDTO.setCategory("Gold");
        pawnItemDTO.setEstimatedValue(1111.11);

        CustomerDTO customerDTO = new CustomerDTO();

        when(customerClient.getCustomerById(1L)).thenReturn(Mono.just(customerDTO));

        Mono<PawnItemDTO> createdPawnItem = pawnItemService.createPawnItem(pawnItemDTO);

        Mono<PawnItemDTO> result = createdPawnItem.flatMap(dto -> pawnItemService.getPawnItemById(dto.getId()));

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getId() != null && // ID должен быть сгенерирован
                                dto.getCustomerId().equals(1L) &&
                                dto.getDescription().equals("Sink") &&
                                dto.getCategory().equals("Gold") &&
                                dto.getEstimatedValue().equals((1111.11)))
                .verifyComplete();
    }
}
