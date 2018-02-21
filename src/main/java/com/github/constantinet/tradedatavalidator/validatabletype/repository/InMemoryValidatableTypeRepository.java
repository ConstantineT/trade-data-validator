package com.github.constantinet.tradedatavalidator.validatabletype.repository;

import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Repository
class InMemoryValidatableTypeRepository implements ValidatableTypeRepository {

    private final Map<String, ValidatableType> data;

    @Autowired
    public InMemoryValidatableTypeRepository(final Collection<ValidatableType> types) {
        this.data = types.stream()
                .collect(Collectors.toMap(ValidatableType::getName, Function.identity()));
    }

    @Override
    public Optional<ValidatableType> find(final String name) {
        return Optional.ofNullable(data.get(name));
    }
}