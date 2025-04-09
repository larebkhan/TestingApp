package com.testingApp.services;

import com.testingApp.TestContainerConfiguration;
import com.testingApp.dto.EmployeeDTO;
import com.testingApp.entities.EmployeeEntity;
import com.testingApp.exceptions.ResourceNotFoundException;
import com.testingApp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeEntity mockEmployee;
    private EmployeeDTO mockEmployeeDto;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        mockEmployee = EmployeeEntity.builder()
                .id(1L)
                .name("Lareb Khan")
                .email("lareb131213@gmail.com")
                .salary(2000L)
                .age(24)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployee, EmployeeDTO.class);

    }
    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto() {
        // assign
            Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        //act
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(id);
        // assert
        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getId()).isEqualTo(id);
        assertThat(employeeDTO.getEmail()).isEqualTo("lareb131213@gmail.com");
    }
    @Test
    void testGetEmployeeById_WhenEmployeeIdIsNotPresent_ThenReturnThrowException(){

        //assign
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(()-> employeeService.getEmployeeById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id : 1");

        verify(employeeRepository).findById(1L);

    }

    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee(){

        //assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(mockEmployee);


        //act
        EmployeeDTO employeeDTO = employeeService.createNewEmployee(mockEmployeeDto);
        ArgumentCaptor<EmployeeEntity> employeeCaptor = ArgumentCaptor.forClass(EmployeeEntity.class);

        verify(employeeRepository).save(employeeCaptor.capture());

        EmployeeEntity capturedEmployee = employeeCaptor.getValue();

        //assert
        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getEmail()).isEqualTo(mockEmployeeDto.getEmail());
        assertThat(capturedEmployee.getName()).isEqualTo("Lareb Khan");
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());
        assertThat(capturedEmployee.getAge()).isEqualTo(24);
        verify(employeeRepository).save((any(EmployeeEntity.class)));

    }
}