package com.country.app.controllers;

import com.country.app.dto.LoginRequest;
import com.country.app.dto.RegisterRequest;
import com.country.app.models.User;
import com.country.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterRequest registerRequest,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            User user = authService.register(registerRequest);
            return "redirect:/api/auth/login?registered";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("loginRequest", new LoginRequest());
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            User user = authService.login(loginRequest);
            return "redirect:/api/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "login";
        }
    }

    // REST API endpoints for programmatic access

    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok().body(Map.of(
                "message", "User registered successfully",
                "username", user.getUsername()
        ));
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return ResponseEntity.ok().body(Map.of(
                "message", "Login successful",
                "username", user.getUsername()
        ));
    }
}