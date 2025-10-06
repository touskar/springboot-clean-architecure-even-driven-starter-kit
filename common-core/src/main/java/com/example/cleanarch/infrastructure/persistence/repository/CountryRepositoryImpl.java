package com.example.cleanarch.common.infrastructure.persistence.repository;

import com.example.cleanarch.common.domain.entities.Country;
import com.example.cleanarch.common.domain.repositories.ICountryRepository;
import com.example.cleanarch.common.infrastructure.database.entities.CountryEntity;
import com.example.cleanarch.common.infrastructure.database.repository.JpaCountryRepository;
import com.example.cleanarch.common.infrastructure.mappers.CountryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CountryRepositoryImpl implements ICountryRepository {
    private final JpaCountryRepository jpaRepository;
    private final CountryMapper countryMapper;

    @Override
    public Country save(Country country) {
        CountryEntity entity = countryMapper.toEntity(Optional.of(country));
        CountryEntity saved = jpaRepository.save(entity);
        return countryMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<Country> findById(String id) {
        return jpaRepository.findById(id).map(e -> countryMapper.toDomain(Optional.of(e)));
    }

    @Override
    public Optional<Country> findByCode(String code) {
        return jpaRepository.findByCode(code).map(e -> countryMapper.toDomain(Optional.of(e)));
    }

    @Override
    public List<Country> findAll() {
        return jpaRepository.findAll().stream()
                .map(e -> countryMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Country> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(e -> countryMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }
}
