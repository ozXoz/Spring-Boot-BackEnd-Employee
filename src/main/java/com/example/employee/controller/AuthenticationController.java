package com.example.employee.controller;

import com.example.employee.model.User;
import com.example.employee.repository.UserRepository;
import com.example.employee.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticationController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        boolean passwordMatches = bCryptPasswordEncoder.matches(user.getPassword(), existingUser.getPassword());
        if (!passwordMatches) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        String token = JwtUtil.createToken(existingUser.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidating session and, optionally, add the token to blacklist
        // ...

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }

}
