package com.country.app.controllers;


import com.country.app.models.User;
import com.country.app.repository.ApiKeyRepository;
import com.country.app.repository.ApiKeyUsageRepository;
import com.country.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyUsageRepository apiKeyUsageRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("totalApiKeys", apiKeyRepository.count());
        model.addAttribute("totalApiCalls", apiKeyUsageRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/users/{userId}")
    public String userDetails(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("apiKeys", apiKeyRepository.findByUser(user));
        return "admin/user-details";
    }

    @PostMapping("/users/{userId}/toggle-status")
    public String toggleUserStatus(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(!user.isActive());
        userRepository.save(user);
        return "redirect:/api/admin/users";
    }

    @GetMapping("/api-usage")
    public String apiUsage(Model model) {
        model.addAttribute("apiKeys", apiKeyRepository.findAll());
        model.addAttribute("apiCalls", apiKeyUsageRepository.findAll());
        return "admin/api-usage";
    }
}