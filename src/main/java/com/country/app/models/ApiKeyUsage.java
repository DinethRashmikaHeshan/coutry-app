package com.country.app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_key_usage")
public class ApiKeyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id", nullable = false)
    private ApiKey apiKey;

    @Column(nullable = false)
    private String endpoint;

    @Column(name = "request_ip")
    private String requestIp;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "response_status")
    private Integer responseStatus;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
}
