package com.country.app.repository;

import com.country.app.models.ApiKey;
import com.country.app.models.ApiKeyUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiKeyUsageRepository extends JpaRepository<ApiKeyUsage, Long> {
    List<ApiKeyUsage> findByApiKey(ApiKey apiKey);

    @Query("SELECT COUNT(u) FROM ApiKeyUsage u WHERE u.apiKey = ?1 AND u.requestedAt >= ?2")
    long countRecentUsagesByApiKey(ApiKey apiKey, LocalDateTime since);

    @Query("SELECT u.endpoint, COUNT(u) as count FROM ApiKeyUsage u WHERE u.apiKey = ?1 GROUP BY u.endpoint ORDER BY count DESC")
    List<Object[]> findMostUsedEndpointsByApiKey(ApiKey apiKey);
}