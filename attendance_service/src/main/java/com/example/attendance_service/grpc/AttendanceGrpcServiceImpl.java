package com.example.attendance_service.grpc;

import com.example.attendance_service.entity.AttendanceRecord;
import com.example.attendance_service.service.AttendanceService;
import com.example.grpc.attendance.AttendanceRequest;
import com.example.grpc.attendance.AttendanceResponse;
import com.example.grpc.attendance.AttendanceServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@GrpcService
public class AttendanceGrpcServiceImpl extends AttendanceServiceGrpc.AttendanceServiceImplBase {

    @Autowired
    AttendanceService attendanceService;

    @Override
    public void streamAttendanceRecords(AttendanceRequest request, StreamObserver<AttendanceResponse> responseObserver) {
        long employeeId = request.getEmployeeId();

        List<AttendanceRecord> records = attendanceService.getAttendanceByEmployeeId(employeeId);

        for (AttendanceRecord record : records) {
            AttendanceResponse response = AttendanceResponse.newBuilder()
                    .setAttendanceDate(record.getAttendanceDate().toString())
                    .setCheckInTime(record.getCheckInTime().toString())
                    .setCheckOutTime(record.getCheckOutTime().toString())
                    .setStatus(record.getStatus())
                    .build();

            // Gửi từng dòng dữ liệu (stream) qua mạng ngay lập tức
            responseObserver.onNext(response);

            try {
                Thread.sleep(500); // Giả lập độ trễ mạng hoặc DB
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Kết thúc stream
        responseObserver.onCompleted();
    }
}