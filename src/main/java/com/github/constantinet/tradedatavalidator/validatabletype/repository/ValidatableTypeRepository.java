package com.github.constantinet.tradedatavalidator.validatabletype.repository;

import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;

import java.util.Optional;

public interface ValidatableTypeRepository {

    Optional<ValidatableType> find(String name);
}