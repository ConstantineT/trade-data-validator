package com.github.constantinet.tradedatavalidator.validator.repository;

import com.github.constantinet.tradedatavalidator.validator.Validator;

import java.util.Optional;

public interface ValidatorRepository {

    Optional<Validator> find(String name);
}