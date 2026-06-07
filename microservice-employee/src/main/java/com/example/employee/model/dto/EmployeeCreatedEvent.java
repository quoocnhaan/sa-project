package com.example.employee.model.dto;

import java.io.Serializable;

public class EmployeeCreatedEvent implements Serializable {
    private Long employeeId;
    private String employeeName;

    // Constructors
    public EmployeeCreatedEvent() {
    }

    public EmployeeCreatedEvent(Long employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    // Getters and Setters
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {}
}
