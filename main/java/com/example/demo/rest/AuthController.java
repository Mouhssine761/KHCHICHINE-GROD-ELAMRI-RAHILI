package com.example.demo.rest;

import com.example.demo.entities.User;
import com.example.demo.model.LoginDto;
import com.example.demo.model.RegisterDto;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")

public class AuthController {

  @Autowired private UserRepository userRepo;
  @Autowired private PasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
    if (userRepo.findByUsername(dto.username()).isPresent()) {
      return ResponseEntity
        .status(409)
        .body("Username already exists");
    }
    User u = new User();
    u.setUsername(dto.username());
    u.setPassword(passwordEncoder.encode(dto.password()));
    userRepo.save(u);
    return ResponseEntity.ok("Registered");
  }

  public record LoginResponse(boolean success, User user, String error) {}

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginDto dto) {
    return userRepo.findByUsername(dto.username())
      .filter(u -> passwordEncoder.matches(dto.password(), u.getPassword()))
      .map(u -> {
        u.setPassword(null);
        return ResponseEntity.ok(new LoginResponse(true, u, null));
      })
      .orElseGet(() ->
        ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponse(false, null, "Invalid username or password"))
      );
  }

}




