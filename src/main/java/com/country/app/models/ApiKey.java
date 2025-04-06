package com.country.app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "api_key", unique = true, nullable = false)
    private String key;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private boolean active;

    @OneToMany(mappedBy = "apiKey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiKeyUsage> usages;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        active = true;
    }
}