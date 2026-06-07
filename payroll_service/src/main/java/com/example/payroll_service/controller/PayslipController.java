package com.example.payroll_service.controller;

import com.example.payroll_service.entity.Payslip;
import com.example.payroll_service.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    @GetMapping
    public List<Payslip> getAllPayslips() {
        return payslipService.getAllPayslips();
    }

    @GetMapping("/{id}")
    public Payslip getPayslipById(@PathVariable Long id) {
        return payslipService.getPayslipById(id);
    }

    @PostMapping
    public Payslip createPayslip(@RequestBody Payslip payslip) {
        return payslipService.createPayslip(payslip);
    }

    @PutMapping("/{id}")
    public Payslip updatePayslip(
            @PathVariable Long id,
            @RequestBody Payslip payslip) {

        return payslipService.updatePayslip(id, payslip);
    }

    @DeleteMapping("/{id}")
    public String deletePayslip(@PathVariable Long id) {

        payslipService.deletePayslip(id);

        return "Payslip deleted successfully";
    }

    @GetMapping("/my-payslip")
    public Payslip getmyPayslip(@AuthenticationPrincipal Jwt jwt){
        Long employeeId =
                ((Number) jwt.getClaim("id")).longValue();
        return payslipService.getMyPayslip(employeeId);
    }
}