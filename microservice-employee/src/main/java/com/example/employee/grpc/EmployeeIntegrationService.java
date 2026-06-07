package com.example.employee.grpc;

import com.example.grpc.attendance.AttendanceRequest;
import com.example.grpc.attendance.AttendanceResponse;
import com.example.grpc.attendance.AttendanceServiceGrpc;
import com.example.grpc.payroll.PayrollServiceGrpc;
import com.example.grpc.payroll.SalaryRequest;
import com.example.grpc.payroll.SalaryResponse;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class EmployeeIntegrationService {

    // Inject Client Stub cho Unary Call
    @GrpcClient("payroll-server")
    private PayrollServiceGrpc.PayrollServiceBlockingStub payrollStub;

    // Inject Client Stub cho Stream Call (Dùng BlockingStub sẽ trả về Iterator cho stream)
    @GrpcClient("attendance-server")
    private AttendanceServiceGrpc.AttendanceServiceBlockingStub attendanceStub;

    /**
     * 1. Ví dụ Unary: Gọi 1 lần lấy thông tin lương
     */
    public String getEmployeeSalaryInfo(long employeeId) {
        SalaryRequest request = SalaryRequest.newBuilder()
                .setEmployeeId(employeeId)
                .build();
        System.out.println("Đang chuẩn bị gọi sang Payroll...");

        try {
            // Thêm timeout 5 giây để tránh treo vô hạn (nếu có)
            SalaryResponse response = payrollStub
                    .withDeadlineAfter(5, java.util.concurrent.TimeUnit.SECONDS)
                    .getCurrentSalary(request);

            return "Salary: " + response.getBaseSalary() + " " + response.getCurrency();
        } catch (Exception e) { // <-- Sửa thành bắt Exception chung
            System.err.println("Đã xảy ra lỗi: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return "Lỗi rùi!";
        }
    }

    /**
     * 2. Ví dụ Server Stream: Nhận luồng dữ liệu trả về liên tục
     */
    public List<String> getEmployeeAttendance(long employeeId) {
        AttendanceRequest request = AttendanceRequest.newBuilder()
                .setEmployeeId(employeeId)
                .setMonth(1)
                .setYear(1)
                .build();

        // GỌI STREAM: Server trả về một Iterator
        Iterator<AttendanceResponse> responseIterator = attendanceStub.streamAttendanceRecords(request);

        List<String> records = new ArrayList<>();

        // Đọc dữ liệu ngay khi server đẩy từng phần tử qua
        while (responseIterator.hasNext()) {
            AttendanceResponse record = responseIterator.next();
            String info = String.format("Date: %s | In: %s | Out: %s | Status: %s",
                    record.getAttendanceDate(), record.getCheckInTime(),
                    record.getCheckOutTime(), record.getStatus());
            records.add(info);
            System.out.println("Received stream chunk: " + info);
        }

        return records;
    }
}
