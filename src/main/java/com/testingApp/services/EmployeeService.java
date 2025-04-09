package com.testingApp.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.testingApp.dto.EmployeeDTO;

public interface EmployeeService {
    EmployeeDTO getEmployeeById(Long id);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO createNewEmployee(EmployeeDTO inputEmployee);
    EmployeeDTO updateEmployeeById(Long employeeId, EmployeeDTO employeeDTO);
    void isExistsByEmployeeId(Long employeeId);
    boolean deleteEmployeeById(Long employeeId);
    EmployeeDTO updatePartialEmployeeById(Long employeeId, Map<String, Object> updates);
}