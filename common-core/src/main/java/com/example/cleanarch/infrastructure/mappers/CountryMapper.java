package com.example.cleanarch.common.infrastructure.mappers;

import com.example.cleanarch.common.domain.entities.Country;
import com.example.cleanarch.common.infrastructure.database.entities.CountryEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CountryMapper implements IDomainMapper<Country, CountryEntity> {

    private final ModelMapper modelMapper;

    @Override
    public Country toDomain(Optional<CountryEntity> entity) {
        return entity.map(e -> modelMapper.map(e, Country.class)).orElse(null);
    }

    @Override
    public CountryEntity toEntity(Optional<Country> domain) {
        return domain.map(d -> modelMapper.map(d, CountryEntity.class)).orElse(null);
    }
}
