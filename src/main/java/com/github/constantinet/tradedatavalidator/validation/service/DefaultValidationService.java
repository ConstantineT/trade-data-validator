package com.github.constantinet.tradedatavalidator.validation.service;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validatabletype.repository.ValidatableTypeRepository;
import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import com.github.constantinet.tradedatavalidator.validator.repository.ValidatorRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
class DefaultValidationService implements ValidationService {

    private final ValidatableTypeRepository validatableTypeRepository;
    private final ValidatorRepository validatorRepository;
    private final MessageConstructionStrategy messageConstructionStrategy;

    @Autowired
    public DefaultValidationService(final ValidatableTypeRepository validatableTypeRepository,
                                    final ValidatorRepository validatorRepository,
                                    final MessageConstructionStrategy messageConstructionStrategy) {
        this.validatableTypeRepository = null;
        this.validatorRepository = null;
        this.messageConstructionStrategy = null;
    }

    @Override
    public Collection<IndexedFailure> validate(final List<JSONObject> objects) {
        return null;
    }
}