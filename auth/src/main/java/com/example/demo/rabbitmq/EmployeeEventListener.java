package com.example.demo.rabbitmq;

import com.example.demo.dto.EmployeeCreatedEvent;
import com.example.demo.entites.Role;
import com.example.demo.entites.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @RabbitListener(queues = "auth.queue")
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("Nhận được yêu cầu tạo tài khoản cho Employee ID: {}, Email: {}",
                event.getEmployeeId(), event.getEmployeeName());

        try {
            // Gọi service để đăng ký người dùng
            if (userRepository.existsByUsername(event.getEmployeeName())) {
                log.error("message", "Error: Username is already taken!");
            }

            User user = new User();
            user.setUsername(event.getEmployeeName());
            user.setEmployeeId(event.getEmployeeId());
            user.setEnabled(true);

            // Hash the password before saving
            user.setPasswordHash(passwordEncoder.encode("123"));

            // 3. Assign default role (e.g., "USER")
            // Note: Make sure this role actually exists in your 'roles' database table!
            Role userRole = roleRepository.findByRoleName("EMPLOYEE")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found in database."));

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            // 4. Save user to database
            userRepository.save(user);

        } catch (Exception e) {
            log.error("Lỗi khi tạo tài khoản cho Employee: {}", e.getMessage());
            // Xử lý retry hoặc gửi vào Dead Letter Queue (DLQ) nếu cần
        }
    }
}