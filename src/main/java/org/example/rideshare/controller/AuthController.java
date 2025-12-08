package org.example.rideshare.controller;

import org.example.rideshare.dto.AuthResponse;
import org.example.rideshare.dto.LoginRequest;
import org.example.rideshare.dto.RegisterRequest;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.UserRepository;
import org.example.rideshare.security.CustomUserDetails;
import org.example.rideshare.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already taken";
        }

        String role = request.getRole();
        if (!"ROLE_USER".equals(role) && !"ROLE_DRIVER".equals(role)) {
            return "Role must be ROLE_USER or ROLE_DRIVER";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt
        user.setRole(role);

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                principal.getUsername(),
                principal.getAuthorities().iterator().next().getAuthority());
    }
}
