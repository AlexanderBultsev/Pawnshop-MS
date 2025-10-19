package com.pawnshop.customerservice;

import com.pawnshop.customerservice.dto.CustomerDTO;
import com.pawnshop.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest(classes = CustomerServiceApplication.class)
@TestPropertySource(properties = {
		"spring.cloud.config.enabled=false", // Отключаем Spring Cloud Config
		"eureka.client.enabled=false", // Отключаем Eureka Client
		"spring.r2dbc.url=r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", // H2 для тестов
		"spring.r2dbc.username=sa",
		"spring.r2dbc.password=",
		"spring.sql.init.mode=always" // Включаем инициализацию SQL-скриптов
})
class CustomerServiceTest {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private DatabaseClient databaseClient;

	@BeforeEach
	void setUp() {
		// Очищаем таблицу customers перед каждым тестом
		databaseClient.sql("TRUNCATE TABLE customers")
				.then()
				.subscribe();
	}

	@Test
	void testCreateCustomer() {
		// Подготовка тестовых данных
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setFirstName("John");
		customerDTO.setLastName("Doe");
		customerDTO.setEmail("john.doe@example.com");

		// Тестирование создания клиента
		Mono<CustomerDTO> result = customerService.createCustomer(customerDTO);

		// Проверка результата
		StepVerifier.create(result)
				.expectNextMatches(dto ->
						dto.getId() != null && // ID должен быть сгенерирован
						dto.getFirstName().equals("John") &&
						dto.getLastName().equals("Doe") &&
						dto.getEmail().equals("john.doe@example.com"))
				.verifyComplete();
	}

	@Test
	void testGetCustomerById() {
		// Подготовка тестовых данных
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setFirstName("John");
		customerDTO.setLastName("Doe");
		customerDTO.setEmail("john.doe@example.com");

		// Сначала создаем клиента
		Mono<CustomerDTO> createdCustomer = customerService.createCustomer(customerDTO);

		// Получаем клиента по ID
		Mono<CustomerDTO> result = createdCustomer.flatMap(dto -> customerService.getCustomerById(dto.getId()));

		// Проверка результата
		StepVerifier.create(result)
				.expectNextMatches(dto ->
						dto.getId() != null &&
						dto.getFirstName().equals("John") &&
						dto.getLastName().equals("Doe") &&
						dto.getEmail().equals("john.doe@example.com"))
				.verifyComplete();
	}

	@Test
	void testGetCustomerByEmail() {
		// Подготовка тестовых данных
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setFirstName("John");
		customerDTO.setLastName("Doe");
		customerDTO.setEmail("john.doe@example.com");

		// Сначала создаем клиента
		Mono<CustomerDTO> createdCustomer = customerService.createCustomer(customerDTO);

		// Получаем клиента по ID
		Mono<CustomerDTO> result = createdCustomer.flatMap(dto -> customerService.getCustomerByEmail(dto.getEmail()));

		// Проверка результата
		StepVerifier.create(result)
				.expectNextMatches(dto ->
						dto.getId() != null &&
								dto.getFirstName().equals("John") &&
								dto.getLastName().equals("Doe") &&
								dto.getEmail().equals("john.doe@example.com"))
				.verifyComplete();
	}
}
