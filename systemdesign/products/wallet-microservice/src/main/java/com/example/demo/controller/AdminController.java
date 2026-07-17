package com.example.demo.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.DatabaseInitializer;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DatabaseInitializer initializer;

    public AdminController(DatabaseInitializer initializer) {
        this.initializer = initializer;
    }

    @PostMapping("/init-db")
    public ResponseEntity<String> initDb() {
        initializer.initializeDatabase();
        return ResponseEntity.accepted().body("database initialization triggered");
    }

    @PostMapping("/seed-db")
    public ResponseEntity<String> seedDb() {
        initializer.seedDatabase();
        return ResponseEntity.accepted().body("database seeding triggered");
    }

    @GetMapping(value = "/dashboard", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> getDashboard() {
        return ResponseEntity.ok(new ClassPathResource("static/admin-dashboard.html"));
    }
}
