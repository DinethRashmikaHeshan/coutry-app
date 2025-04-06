package com.country.app.repository;

import com.country.app.models.ApiKey;
import com.country.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKey(String key);
    List<ApiKey> findByUser(User user);
    List<ApiKey> findByUserAndActive(User user, boolean active);
    boolean existsByKey(String key);
}