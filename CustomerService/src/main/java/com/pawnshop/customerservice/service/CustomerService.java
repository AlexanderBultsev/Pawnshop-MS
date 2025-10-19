package com.pawnshop.customerservice.service;

import com.pawnshop.customerservice.dto.CustomerDTO;
import com.pawnshop.customerservice.entity.Customer;
import com.pawnshop.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    // Создание нового клиента
    public Mono<CustomerDTO> createCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());

        return customerRepository.save(customer)
                .map(CustomerDTO::fromEntity);
    }

    // Получение клиента по ID
    public Mono<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id) // Поиск в БД
                .map(CustomerDTO::fromEntity);
    }

    // Получение клиента по Email
    public Mono<CustomerDTO> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(CustomerDTO::fromEntity);
    }
}
