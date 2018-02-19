package com.github.constantinet.tradedatavalidator.validator.repository;

import com.github.constantinet.tradedatavalidator.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.constantinet.tradedatavalidator.TradeDataValidatorApplication.VALIDATORS_QUALIFIER;

@Repository
class InMemoryValidatorRepository implements ValidatorRepository {

    private final Map<String, Validator> data;

    @Autowired
    public InMemoryValidatorRepository(@Qualifier(VALIDATORS_QUALIFIER) final Collection<Validator> validators) {
        this.data = validators.stream()
                .collect(Collectors.toMap(Validator::getName, Function.identity()));
    }

    @Override
    public Optional<Validator> find(final String name) {
        return Optional.ofNullable(data.get(name));
    }
}