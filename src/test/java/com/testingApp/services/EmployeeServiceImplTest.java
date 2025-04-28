package com.testingApp.services;

import com.testingApp.TestContainerConfiguration;
import com.testingApp.dto.EmployeeDTO;
import com.testingApp.entities.EmployeeEntity;
import com.testingApp.exceptions.ResourceNotFoundException;
import com.testingApp.repositories.EmployeeRepository;
import com.testingApp.services.Impl.EmployeeServiceImpl;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    void testCreateNewEmployee_WhenEmployeeAlreadyPresent_ThenReturnThrowException(){

        //act and assert
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of(mockEmployee));
        assertThatThrownBy(()-> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already present with that email");
        verify(employeeRepository).findByEmail(mockEmployee.getEmail());
        verify(employeeRepository, never()).save(any());

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

    @Test
    void testUpdateEmployee_when_EmployeeDoesNotExistThenThrowException(){
        Long id = 1L;
        when(employeeRepository.existsById(id)).thenReturn(true);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(mockEmployee);

        EmployeeDTO employeeDTO = employeeService.updateEmployeeById(id,mockEmployeeDto);

        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository).save(any(EmployeeEntity.class));

    }

    @Test
    void testDeleteEmployeeById_whenEmployeeIsNotPresent_thenReturnEmployee(){

        Long id = 1L;
        when(employeeRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() ->  employeeService.deleteEmployeeById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository, never()).deleteById(id);

    }

    @Test
    void testDeleteEmployeeById_WhenEmployeeIsPresent_thenDoNptThrowException(){
        Long id = 1L;
        when(employeeRepository.existsById(id)).thenReturn(true);

        assertThatCode(()-> employeeService.deleteEmployeeById(id))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(id);


    }

    @Test
    void tesGetAllEmployees_thenReturnListOfEmployee(){

        //Arrange
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .id(2L)
                .name("John Doe")
                .email("john@gmail.com")
                .salary(20522L)
                .age(25)
                .build();

        List<EmployeeEntity> li = List.of(mockEmployee, employeeEntity);
        when(employeeRepository.findAll()).thenReturn(li);

        //act
        List<EmployeeDTO> employeeDTOS = employeeService.getAllEmployees();

        //assert
        assertThat(employeeDTOS).isNotNull();
        assertThat(employeeDTOS.get(0).getName()).isEqualTo("Lareb Khan");

    }

    @Test
    void testUpdatePartialEmployeeById_WhenEmployeeExists_thenReturnUpdatedEmployee(){

        Long id = 1L;
        //arrange
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "John Wick");
        updates.put("salary", 5000L);


        when(employeeRepository.existsById(id)).thenReturn(true);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(mockEmployee);

        //act
        EmployeeDTO employeeDTO = employeeService.updatePartialEmployeeById(id,updates);


        //assert

        assertThat(employeeDTO).isNotNull();
        assertThat(employeeDTO.getName()).isEqualTo("John Wick");
        assertThat(employeeDTO.getSalary()).isEqualTo(5000L);
    }

    @Test
    void testUpdatePartialEmployeeById_WhenEmployeeNotExists_thenThrowException(){
        Long id = 1L;
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "John Wick");
        updates.put("salary", 5000L);
        when(employeeRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(()-> employeeService.updatePartialEmployeeById(id,updates))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");



    }
















}