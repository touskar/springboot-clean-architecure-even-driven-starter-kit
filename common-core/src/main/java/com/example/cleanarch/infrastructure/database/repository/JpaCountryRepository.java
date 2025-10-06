package com.example.cleanarch.common.infrastructure.database.repository;

import com.example.cleanarch.common.infrastructure.database.entities.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCountryRepository extends JpaRepository<CountryEntity, String> {
    Optional<CountryEntity> findByCode(String code);

    @Query("SELECT c FROM CountryEntity c WHERE c.status = 'ACTIVE'")
    List<CountryEntity> findAllActive();

    boolean existsByCode(String code);
}
