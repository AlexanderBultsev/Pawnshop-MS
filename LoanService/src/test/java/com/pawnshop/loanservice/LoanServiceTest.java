package com.pawnshop.loanservice;

import com.pawnshop.customerservice.dto.CustomerDTO;
import com.pawnshop.loanservice.client.CustomerClient;
import com.pawnshop.loanservice.client.PawnItemClient;
import com.pawnshop.loanservice.dto.LoanDTO;
import com.pawnshop.loanservice.service.LoanService;
import com.pawnshop.pawnitemservice.dto.PawnItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = LoanServiceApplication.class)
@TestPropertySource(properties = {
		"spring.cloud.config.enabled=false", // Отключаем Spring Cloud Config
		"eureka.client.enabled=false", // Отключаем Eureka Client
		"spring.r2dbc.url=r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", // H2 для тестов
		"spring.r2dbc.username=sa",
		"spring.r2dbc.password=",
		"spring.sql.init.mode=always" // Включаем инициализацию SQL-скриптов
})

class LoanServiceTest {

	@Autowired
	private LoanService loanService;

	@Autowired
	private DatabaseClient databaseClient;

	@MockBean
	private CustomerClient customerClient;

	@MockBean
	private PawnItemClient pawnItemClient;

	@BeforeEach
	void setUp() {
		// Очищаем таблицу loans перед каждым тестом
		databaseClient.sql("TRUNCATE TABLE loans")
				.then()
				.subscribe();
	}

	@Test
	void testGetLoanById() {
		// Подготовка данных
		LoanDTO loanDTO = new LoanDTO();
		loanDTO.setCustomerId(1L);
		loanDTO.setPawnItemId(1L);
		loanDTO.setPercentAmount(0.3);

		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setId(1L);

		PawnItemDTO itemDTO = new PawnItemDTO();
		itemDTO.setId(1L);
		itemDTO.setEstimatedValue(1000.0);
		itemDTO.setStatus("IN_PLEDGE");

		when(customerClient.getCustomerById(1L)).thenReturn(Mono.just(customerDTO));
		when(pawnItemClient.getPawnItemById(1L)).thenReturn(Mono.just(itemDTO));

		Mono<LoanDTO> createdLoan = loanService.createLoan(loanDTO);

		Mono<LoanDTO> result = createdLoan.flatMap(dto -> loanService.getLoanById(dto.getId()));

		// Проверка результата
		StepVerifier.create(result)
				.expectNextMatches(dto ->
						dto.getId() != null &&
						dto.getAmount().equals(1000.0) &&
						dto.getTotalAmount().equals(10000.0))
				.verifyComplete();
	}
}
