package com.github.constantinet.tradedatavalidator.validator.repository;

import com.github.constantinet.tradedatavalidator.validator.DummyValidator;
import com.github.constantinet.tradedatavalidator.validator.Validator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

public class InMemoryValidatableTypeRepositoryTest {

    private static final String VALIDATOR_1_NAME = "testValidator1";
    private static final String VALIDATOR_2_NAME = "testValidator2";

    private InMemoryValidatorRepository validatorRepository;

    @Before
    public void setUp() {
        validatorRepository = new InMemoryValidatorRepository(Arrays.asList(
                new DummyValidator(VALIDATOR_1_NAME),
                new DummyValidator(VALIDATOR_2_NAME)));
    }

    @Test
    public void testFind_shouldReturnCorrectType_whenExistingNamePassed() {
        // given
        final String givenName = VALIDATOR_2_NAME;

        // when
        final Optional<Validator> validatableType = validatorRepository.find(givenName);

        // then
        assertThat(validatableType, notNullValue());
        assertThat(validatableType.get(), allOf(
                hasProperty("name", equalTo(givenName)),
                hasProperty("class", equalTo(DummyValidator.class))
        ));
    }

    @Test
    public void testFind_shouldReturnEmptyOptional_whenNotExistingNamePassed() {
        // given
        final String givenName = "testValidator3";

        // when
        final Optional<Validator> validatableType = validatorRepository.find(givenName);

        // then
        assertThat(validatableType, notNullValue());
        assertThat(validatableType.isPresent(), equalTo(false));
    }

    @Test
    public void testFind_shouldReturnEmptyOptional_whenNullPassed() {
        // when
        final Optional<Validator> validatableType = validatorRepository.find(null);

        // then
        assertThat(validatableType, notNullValue());
        assertThat(validatableType.isPresent(), equalTo(false));
    }
}