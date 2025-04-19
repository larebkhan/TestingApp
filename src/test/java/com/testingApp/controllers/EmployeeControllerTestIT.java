package com.testingApp.controllers;

import com.testingApp.TestContainerConfiguration;
import com.testingApp.advices.ApiResponse;
import com.testingApp.dto.EmployeeDTO;
import com.testingApp.entities.EmployeeEntity;
import com.testingApp.repositories.EmployeeRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeControllerTestIT  extends  AbstractIntegerationTest{



    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeEntity testEmployee;
    private EmployeeDTO testEmployeeDTO;

    @BeforeEach
    void setUp(){

        testEmployee = EmployeeEntity.builder()

                .name("Lareb Khan")
                .email("lareb131213@gmail.com")
                .salary(2000L)
                .age(24)
                .role("USER")
                .isActive(true)
                .dateOfJoining(LocalDate.now())
                .build();

        testEmployeeDTO = EmployeeDTO.builder()

                .name("Lareb Khan")
                .email("lareb131213@gmail.com")
                .salary(2000L)
                .age(24)
                .role("USER")
                .isActive(true)
                .dateOfJoining(LocalDate.now())
                .build();
        employeeRepository.deleteAll();

    }

    @Test
    void testGetEmployeeByIdSuccess(){
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<EmployeeDTO>>() {
                })
                //.isEqualTo(testEmployeeDTO);
                .value(response -> {
                    EmployeeDTO employeeDTO = response.getData();
                    assertThat(savedEmployee.getEmail()).isEqualTo(employeeDTO.getEmail());
                    assertThat(employeeDTO.getId()).isEqualTo(savedEmployee.getId());
                });
    }

    @Test
    void testGetEmployeeById(){
        webTestClient.get()
                .uri("employees/1")
                .exchange()
                .expectStatus().isNotFound();

    }
    @Test
    void testCreateNewEmployee_WhenEmployeeAlreadyExists(){
        EmployeeEntity employee = employeeRepository.save(testEmployee);
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDTO)
                .exchange()
                .expectStatus().is5xxServerError();

    }
    @Test
    void testCreateNewEmployee_WhenEmployeeDoesNotExistThenCreateNewEmployee(){
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.email").isEqualTo(testEmployeeDTO.getEmail())
                .jsonPath("$.data.name").isEqualTo(testEmployeeDTO.getName());
    }

    @Test
    void testUpdateEmployee_WhenEmployeeDoesNotExistThenThrowException(){
        webTestClient.patch()
                .uri("/employees/999")
                .bodyValue(testEmployeeDTO)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_WhenEmployeeExist(){
        EmployeeEntity employee = employeeRepository.save(testEmployee);
        testEmployeeDTO.setName("Ayan Khan");
        testEmployeeDTO.setSalary(250L);
        webTestClient.put()
                .uri("/employees/{id}", employee.getId())
                .bodyValue(testEmployeeDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDTO.class);

    }

    @Test
    void testDeleteEmployeeById_WhenEmployeeDoesNotExist(){
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }
    @Test
    void testDeleteEmployeeById_WhenEmployeeIsPresent(){
        EmployeeEntity employee = employeeRepository.save(testEmployee);
        webTestClient.delete()
                .uri("/employees/{id}", employee.getId())
                .exchange()
                .expectStatus().isOk();
    }

}