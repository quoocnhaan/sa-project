package com.example.attendance_service.repository;

import com.example.attendance_service.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> getAttendanceRecordsByEmployeeId(Long id);

}