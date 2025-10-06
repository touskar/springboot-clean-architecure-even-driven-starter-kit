package com.example.cleanarch.common.domain.repositories;

import com.example.cleanarch.common.domain.entities.Country;
import java.util.List;
import java.util.Optional;

public interface ICountryRepository {
    Country save(Country country);
    Optional<Country> findById(String id);
    Optional<Country> findByCode(String code);
    List<Country> findAll();
    List<Country> findAllActive();
    void deleteById(String id);
    boolean existsByCode(String code);
}
