package com.github.constantinet.tradedatavalidator.validatabletype.repository;

import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;

public class InMemoryValidatableTypeRepositoryTest {

    private static final String GIVEN_TYPE_1_NAME = "testType1";
    private static final String GIVEN_TYPE_2_NAME = "testType2";
    private static final String VALIDATOR_NAME_1 = "testValidator1";
    private static final String VALIDATOR_NAME_2 = "testValidator2";
    private static final String VALIDATOR_NAME_3 = "testValidator3";

    private InMemoryValidatableTypeRepository validatableTypeRepository;

    @Before
    public void setUp() {
        validatableTypeRepository = new InMemoryValidatableTypeRepository(Arrays.asList(
                new ValidatableType(GIVEN_TYPE_1_NAME, Arrays.asList(VALIDATOR_NAME_1, VALIDATOR_NAME_2)),
                new ValidatableType(GIVEN_TYPE_2_NAME, Arrays.asList(VALIDATOR_NAME_2, VALIDATOR_NAME_3))));
    }

    @Test
    public void testFind_shouldReturnCorrectType_whenExistingNamePassed() {
        // given
        final String givenName = GIVEN_TYPE_2_NAME;

        // when
        final Optional<ValidatableType> validatableType = validatableTypeRepository.find(givenName);

        // then
        assertThat(validatableType, notNullValue());
        assertThat(validatableType.get(), allOf(
                hasProperty("name", equalTo(givenName)),
                hasProperty("validatorNames", contains(VALIDATOR_NAME_2, VALIDATOR_NAME_3))
        ));
    }

    @Test
    public void testFind_shouldReturnEmptyOptional_whenNotExistingNamePassed() {
        // given
        final String givenName = "testType3";

        // when
        final Optional<ValidatableType> validatableType = validatableTypeRepository.find(givenName);

        // then
        assertThat(validatableType, notNullValue());
        assertThat(validatableType.isPresent(), equalTo(false));
    }

    @Test
    public void testFind_shouldReturnEmptyOptional_whenNullPassed() {
        // when
        final Optional<ValidatableType> validatableType = validatableTypeRepository.find(null);

        // then
        assertThat(validatableType, notNullValue());
        assertThat(validatableType.isPresent(), equalTo(false));
    }
}