package com.example.attendance_service.controller;

import com.example.attendance_service.entity.AttendanceRecord;
import com.example.attendance_service.service.AttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('HR')")
    public AttendanceRecord createAttendanceRecord(
            @RequestBody AttendanceRecord attendanceRecord) {

        return attendanceService.createAttendanceRecord(attendanceRecord);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
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


//    @GetMapping("/myAttendance")
//    public List<AttendanceRecord> getMyAttendance(){
//        return attendanceService.getMyAttendance();
//    }
}