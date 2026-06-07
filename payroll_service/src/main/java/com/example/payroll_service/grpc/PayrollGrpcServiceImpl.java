package com.example.payroll_service.grpc;

import com.example.grpc.payroll.PayrollServiceGrpc;
import com.example.grpc.payroll.SalaryRequest;
import com.example.grpc.payroll.SalaryResponse;
import com.example.payroll_service.entity.Salary;
import com.example.payroll_service.repository.SalaryRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService // Annotation của Spring Boot gRPC giúp tự động đăng ký service
public class PayrollGrpcServiceImpl extends PayrollServiceGrpc.PayrollServiceImplBase {

    @Autowired
    SalaryRepository salaryRepository;

    @Override
    public void getCurrentSalary(SalaryRequest request, StreamObserver<SalaryResponse> responseObserver) {
        System.out.println("Payroll Server đã nhận được request cho Employee ID: " + request.getEmployeeId());
        long employeeId = request.getEmployeeId();

        Salary salary = salaryRepository.findByEmployeeId(request.getEmployeeId());

        SalaryResponse response = SalaryResponse.newBuilder()
                .setBaseSalary(salary.getBaseSalary().doubleValue())
                .setCurrency(salary.getCurrency())
                .setEffectiveFrom(salary.getEffectiveFrom().toString())
                .build();

        // Trả kết quả về cho client
        responseObserver.onNext(response);
        // Đóng kết nối
        responseObserver.onCompleted();
    }
}
