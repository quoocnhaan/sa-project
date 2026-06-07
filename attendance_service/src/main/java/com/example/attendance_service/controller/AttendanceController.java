package com.example.attendance_service.controller;

import com.example.attendance_service.entity.AttendanceRecord;
import com.example.attendance_service.entity.AttendanceRecordRequest;
import com.example.attendance_service.service.AttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@AllArgsConstructor
public class AttendanceController {

    AttendanceService attendanceService;

    @GetMapping
    public List<AttendanceRecord> getAllAttendanceRecords() {
        return attendanceService.getAllAttendanceRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public AttendanceRecord getAttendanceRecordById(@PathVariable Long id) {
        return attendanceService.getAttendanceRecordById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'EMPLOYEE')")
    public AttendanceRecord createAttendanceRecord(
            @RequestBody AttendanceRecordRequest request,
            @AuthenticationPrincipal Jwt jwt) { // Inject JWT vào đây

        // 1. Lấy employeeId từ token một cách bảo mật
        Long employeeId = ((Number) jwt.getClaim("id")).longValue();

        // 2. Khởi tạo và Map dữ liệu từ Request DTO sang Entity
        AttendanceRecord attendanceRecord = new AttendanceRecord();

        // Gán ID lấy từ token
        attendanceRecord.setEmployeeId(employeeId);

        // Gán các trường từ Request Body
        attendanceRecord.setAttendanceDate(request.getAttendanceDate());
        attendanceRecord.setCheckInTime(request.getCheckInTime());
        attendanceRecord.setCheckOutTime(request.getCheckOutTime());
        attendanceRecord.setStatus(request.getStatus());

        // Khởi tạo thời gian tạo bản ghi (CreatedAt)
        attendanceRecord.setCreatedAt(LocalDateTime.now());

        // 3. Gọi service để lưu vào database
        return attendanceService.createAttendanceRecord(attendanceRecord);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR')")
    public AttendanceRecord updateAttendanceRecord(
            @PathVariable Long id,
            @RequestBody AttendanceRecord attendanceRecord) {

        return attendanceService.updateAttendanceRecord(id, attendanceRecord);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public String deleteAttendanceRecord(@PathVariable Long id) {

        attendanceService.deleteAttendanceRecord(id);

        return "Attendance record deleted successfully";
    }

    @PutMapping("/by-date")
    @PreAuthorize("hasAnyRole('HR', 'EMPLOYEE')")
    public AttendanceRecord updateAttendanceRecordByDate(
            @RequestBody AttendanceRecordRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        // Extract employeeId securely from the token
        Long employeeId = ((Number) jwt.getClaim("id")).longValue();

        // Pass to service
        return attendanceService.updateAttendanceByDate(employeeId, request);
    }


//    @GetMapping("/myAttendance")
//    public List<AttendanceRecord> getMyAttendance(){
//        return attendanceService.getMyAttendance();
//    }
}