package com.testingApp.repositories;

import com.testingApp.TestContainerConfiguration;
import com.testingApp.entities.EmployeeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestContainerConfiguration.class)
@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeEntity employee;

    @BeforeEach
    void setUp(){
        employee = EmployeeEntity.builder()
                .id(1L)
                .name("Lareb")
                .email("lareb@gmail.com")
                .salary(1000L)
                .build();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee(){
//  Arrange
        employeeRepository.save(employee);

//  Act
        List<EmployeeEntity> employeeEntityList = employeeRepository.findByEmail(employee.getEmail());

//  Assert
        assertThat(employeeEntityList).isNotEmpty();
        assertThat(employeeEntityList).isNotNull();
        assertThat(employeeEntityList.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList(){
        String email = "notPresent.123@gmail.com";
        List<EmployeeEntity> employeeEntityList = employeeRepository.findByEmail(email);
        assertThat(employeeEntityList).isNotNull();
        assertThat(employeeEntityList).isEmpty();

    }
}
