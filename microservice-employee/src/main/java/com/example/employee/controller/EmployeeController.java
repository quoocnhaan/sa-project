package com.example.employee.controller;

import com.example.employee.grpc.EmployeeIntegrationService;
import com.example.employee.model.dto.EmployeeDTO;
import com.example.employee.model.dto.EmployeeResponse;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.EmployeeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    private final EmployeeServiceImpl service;

    @Autowired
    private EmployeeIntegrationService integrationService;

    public EmployeeController(EmployeeService employeeService, EmployeeServiceImpl service) {
        this.employeeService = employeeService;
        this.service = service;
    }

    @GetMapping("/my-salary")
    public String getSalary(
            @AuthenticationPrincipal Jwt jwt) {

        Long employeeId =
                ((Number) jwt.getClaim("id")).longValue();

        return integrationService.getEmployeeSalaryInfo(employeeId);
    }


    // Test Stream Call (Chấm công)
    @GetMapping("/{id}/attendance")
    public List<String> getAttendance(
            @PathVariable("id") long id)
    {
        return integrationService.getEmployeeAttendance(id);
    }

    @GetMapping("/my-attendance")
    public List<String> getAttendance(
            @AuthenticationPrincipal Jwt jwt) {

        Long employeeId =
                ((Number) jwt.getClaim("id")).longValue();

        return integrationService.getEmployeeAttendance(employeeId);
    }



    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        logger.info("Request received: Fetching all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeResponse> getMyProfile() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        logger.info("Request received: Fetching profile for current user: '{}'", username);
        EmployeeResponse employee = employeeService.getEmployeeByUsername(username);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        logger.info("Request received: Fetching details for employee ID: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        logger.info("Request received: Creating new employee with code: {}", employeeDTO.getEmployeeCode());
        EmployeeDTO created = employeeService.createEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        logger.info("Request received: Updating employee details for ID: {}", id);
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        logger.info("Request received: Deleting employee with ID: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-profile")
    public ResponseEntity<EmployeeResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {
        Long employeeId =
                ((Number) jwt.getClaim("id")).longValue();
        EmployeeResponse dto = service.getMyProfile(employeeId);

        return ResponseEntity.ok(dto);
    }
}
