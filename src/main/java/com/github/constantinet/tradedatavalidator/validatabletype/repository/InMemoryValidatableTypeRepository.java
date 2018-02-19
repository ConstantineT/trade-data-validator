package com.github.constantinet.tradedatavalidator.validatabletype.repository;

import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.TradeDataValidatorApplication.TYPES_QUALIFIER;

@Repository
class InMemoryValidatableTypeRepository implements ValidatableTypeRepository {

    private final Map<String, ValidatableType> data;

    @Autowired
    public InMemoryValidatableTypeRepository(@Qualifier(TYPES_QUALIFIER) final Collection<ValidatableType> types) {
        this.data = null;

    }

    @Override
    public Optional<ValidatableType> find(final String name) {
        return null;
    }
}