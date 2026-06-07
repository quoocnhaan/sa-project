package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
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

}
