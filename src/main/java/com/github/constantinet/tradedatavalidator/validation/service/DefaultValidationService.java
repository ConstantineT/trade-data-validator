package com.github.constantinet.tradedatavalidator.validation.service;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import com.github.constantinet.tradedatavalidator.validatabletype.repository.ValidatableTypeRepository;
import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import com.github.constantinet.tradedatavalidator.validator.Validator;
import com.github.constantinet.tradedatavalidator.validator.repository.ValidatorRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.constantinet.tradedatavalidator.ComponentNames.DEFAULT_MESSAGE_STRATEGY_QUALIFIER;
import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.CAN_NOT_VALIDATE_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.TYPE_NOT_SUPPORTED_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.CAN_NOT_VALIDATE_KEY;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.TYPE_NOT_SUPPORTED_KEY;
import static com.github.constantinet.tradedatavalidator.Properties.Names.TYPE_PROPERTY_NAME;

@Service
class DefaultValidationService implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultValidationService.class);

    private final ValidatableTypeRepository validatableTypeRepository;
    private final ValidatorRepository validatorRepository;
    private final MessageConstructionStrategy messageConstructionStrategy;

    @Autowired
    public DefaultValidationService(final ValidatableTypeRepository validatableTypeRepository,
                                    final ValidatorRepository validatorRepository,
                                    @Qualifier(DEFAULT_MESSAGE_STRATEGY_QUALIFIER)
                                        final MessageConstructionStrategy messageConstructionStrategy) {
        this.validatableTypeRepository = validatableTypeRepository;
        this.validatorRepository = validatorRepository;
        this.messageConstructionStrategy = messageConstructionStrategy;
    }

    @Override
    public Collection<IndexedFailure> validate(final List<JSONObject> objects) {
        Objects.requireNonNull(objects, "objects to validate can not be null");
        Assert.noNullElements(objects.toArray(), "objects to validate can not contain nulls");

        return IntStream.range(0, objects.size())
                .mapToObj(index -> Pair.of(index, objects.get(index)))
                .map(this::getTypeTriple)
                .map(this::getIndexedFailure)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Triple<Integer, JSONObject, Optional<ValidatableType>> getTypeTriple(
            final Pair<Integer, JSONObject> pair) {
        final String type;
        try {
            type = pair.getRight().getString(TYPE_PROPERTY_NAME);
        } catch (final JSONException ex) {
            LOG.warn("Can not retrieve a type from " + pair.getRight(), ex);
            return Triple.of(pair.getLeft(), pair.getRight(), Optional.empty());
        }
        return Triple.of(pair.getLeft(), pair.getRight(), validatableTypeRepository.find(type));
    }

    private Optional<IndexedFailure> getIndexedFailure(
            final Triple<Integer, JSONObject, Optional<ValidatableType>> triple) {
        if (triple.getRight().isPresent()) {
            return getIndexedFailureForType(Triple.of(triple.getLeft(), triple.getMiddle(), triple.getRight().get()));
        } else {
            return Optional.of(
                    getIndexedFailureFromKey(triple.getLeft(), TYPE_NOT_SUPPORTED_KEY, TYPE_NOT_SUPPORTED_MESSAGE));
        }
    }

    private Optional<IndexedFailure> getIndexedFailureForType(
            final Triple<Integer, JSONObject, ValidatableType> triple) {
        final List<Optional<Validator>> validatorOptionals = triple.getRight().getValidatorNames().stream()
                .map(validatorName -> {
                    Optional<Validator> validator = validatorRepository.find(validatorName);
                    if (!validator.isPresent()) {
                        LOG.warn("Can not find a validator with name " + validatorName);
                    }
                    return validator;
                })
                .collect(Collectors.toList());

        if (validatorOptionals.stream().allMatch(Optional::isPresent)) {
            final List<String> failures = validatorOptionals.stream()
                    .map(Optional::get)
                    .map(validator -> validator.validate(triple.getMiddle()))
                    .filter(validationResult -> !validationResult.isSucceeded())
                    .flatMap(validationResult -> validationResult.getFailures().stream())
                    .collect(Collectors.toList());
            return failures.isEmpty() ? Optional.empty() : Optional.of(new IndexedFailure(triple.getLeft(), failures));
        } else {
            return Optional.of(
                    getIndexedFailureFromKey(triple.getLeft(), CAN_NOT_VALIDATE_KEY, CAN_NOT_VALIDATE_MESSAGE));
        }
    }

    private IndexedFailure getIndexedFailureFromKey(final int index, final String key, final String defaultMsg) {
        final String message = messageConstructionStrategy.constructMessage(key, defaultMsg, null);
        return new IndexedFailure(index, Collections.singletonList(message));
    }
}