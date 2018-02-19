package com.github.constantinet.tradedatavalidator.validation.service;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import com.github.constantinet.tradedatavalidator.validatabletype.repository.ValidatableTypeRepository;
import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import com.github.constantinet.tradedatavalidator.validator.Validator;
import com.github.constantinet.tradedatavalidator.validator.repository.ValidatorRepository;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.CAN_NOT_VALIDATE_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.TYPE_NOT_SUPPORTED_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.CAN_NOT_VALIDATE_KEY;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.TYPE_NOT_SUPPORTED_KEY;
import static com.github.constantinet.tradedatavalidator.PropertyNames.TYPE_PROPERTY_NAME;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultValidationServiceTest {

    private static final String GIVEN_TYPE_1 = "testType1";
    private static final String GIVEN_TYPE_2 = "testType2";
    private static final String GIVEN_VALIDATOR_1_NAME = "testValidator1";
    private static final String GIVEN_VALIDATOR_2_NAME = "testValidator2";
    private static final String GIVEN_VALIDATOR_3_NAME = "testValidator3";

    private final ValidatableType givenValidatableType1
            = new ValidatableType(GIVEN_TYPE_1, Arrays.asList(GIVEN_VALIDATOR_2_NAME, GIVEN_VALIDATOR_1_NAME));
    private final ValidatableType givenValidatableType2
            = new ValidatableType(GIVEN_TYPE_2, Collections.singletonList(GIVEN_VALIDATOR_3_NAME));
    private final Validator givenValidator1 = mock(Validator.class);
    private final Validator givenValidator2 = mock(Validator.class);
    private final Validator givenValidator3 = mock(Validator.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ValidatableTypeRepository validatableTypeRepository;

    @Mock
    private ValidatorRepository validatorRepository;

    @Mock
    private MessageConstructionStrategy messageConstructionStrategy;

    @InjectMocks
    private DefaultValidationService validationService;

    @Test
    public void testValidate_shouldReturnCorrectResult_whenValidInputPassed() {
        // given
        final JSONObject givenInput1 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_1));
        final JSONObject givenInput2 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_2));
        when(validatableTypeRepository.find(GIVEN_TYPE_1)).thenReturn(Optional.of(givenValidatableType1));
        when(validatableTypeRepository.find(GIVEN_TYPE_2)).thenReturn(Optional.of(givenValidatableType2));
        when(givenValidator1.validate(givenInput1)).thenReturn(new ValidationResult(true, Collections.emptyList()));
        when(givenValidator2.validate(givenInput1)).thenReturn(new ValidationResult(true, Collections.emptyList()));
        when(givenValidator3.validate(givenInput2)).thenReturn(new ValidationResult(true, Collections.emptyList()));
        when(validatorRepository.find(GIVEN_VALIDATOR_1_NAME)).thenReturn(Optional.of(givenValidator1));
        when(validatorRepository.find(GIVEN_VALIDATOR_2_NAME)).thenReturn(Optional.of(givenValidator2));
        when(validatorRepository.find(GIVEN_VALIDATOR_3_NAME)).thenReturn(Optional.of(givenValidator3));

        // when
        final Collection<IndexedFailure> indexedFailures
                = validationService.validate(Arrays.asList(givenInput1, givenInput2));

        // then
        assertThat(indexedFailures, emptyIterableOf(IndexedFailure.class));
    }

    @Test
    public void testValidate_shouldReturnCorrectResult_whenNotExistingTypeInInputPassed() {
        // given
        final JSONObject givenInput1 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_1));
        final JSONObject givenInput2 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_2));
        when(validatableTypeRepository.find(GIVEN_TYPE_1)).thenReturn(Optional.of(givenValidatableType1));
        when(validatableTypeRepository.find(GIVEN_TYPE_2)).thenReturn(Optional.empty());
        when(givenValidator1.validate(givenInput1)).thenReturn(new ValidationResult(true, Collections.emptyList()));
        when(givenValidator2.validate(givenInput1)).thenReturn(new ValidationResult(true, Collections.emptyList()));
        when(messageConstructionStrategy.constructMessage(
                eq(TYPE_NOT_SUPPORTED_KEY), eq(TYPE_NOT_SUPPORTED_MESSAGE), any())).thenReturn("error!");
        when(validatorRepository.find(GIVEN_VALIDATOR_1_NAME)).thenReturn(Optional.of(givenValidator1));
        when(validatorRepository.find(GIVEN_VALIDATOR_2_NAME)).thenReturn(Optional.of(givenValidator2));

        // when
        final Collection<IndexedFailure> indexedFailures
                = validationService.validate(Arrays.asList(givenInput1, givenInput2));

        // then
        assertThat(indexedFailures, contains(allOf(
                hasProperty("index", equalTo(1)),
                hasProperty("failures", contains("error!"))
        )));
    }

    @Test
    public void testValidate_shouldReturnCorrectResult_whenValidatorNotFoundForPassedInput() {
        // given
        final JSONObject givenInput1 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_1));
        final JSONObject givenInput2 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_2));
        when(validatableTypeRepository.find(GIVEN_TYPE_1)).thenReturn(Optional.of(givenValidatableType1));
        when(validatableTypeRepository.find(GIVEN_TYPE_2)).thenReturn(Optional.of(givenValidatableType2));
        when(messageConstructionStrategy.constructMessage(
                eq(CAN_NOT_VALIDATE_KEY), eq(CAN_NOT_VALIDATE_MESSAGE), any())).thenReturn("error!");
        when(givenValidator3.validate(givenInput2)).thenReturn(new ValidationResult(true, Collections.emptyList()));
        when(validatorRepository.find(GIVEN_VALIDATOR_1_NAME)).thenReturn(Optional.of(givenValidator1));
        when(validatorRepository.find(GIVEN_VALIDATOR_2_NAME)).thenReturn(Optional.empty());
        when(validatorRepository.find(GIVEN_VALIDATOR_3_NAME)).thenReturn(Optional.of(givenValidator3));

        // when
        final Collection<IndexedFailure> indexedFailures
                = validationService.validate(Arrays.asList(givenInput1, givenInput2));

        // then
        assertThat(indexedFailures, contains(allOf(
                hasProperty("index", equalTo(0)),
                hasProperty("failures", contains("error!"))
        )));
    }

    @Test
    public void testValidate_shouldReturnCorrectResult_whenValidationFailuresReported() {
        // given
        final JSONObject givenInput1 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_1));
        final JSONObject givenInput2 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_2));
        when(validatableTypeRepository.find(GIVEN_TYPE_1)).thenReturn(Optional.of(givenValidatableType1));
        when(validatableTypeRepository.find(GIVEN_TYPE_2)).thenReturn(Optional.of(givenValidatableType2));
        when(givenValidator1.validate(givenInput1)).thenReturn(
                new ValidationResult(false, Arrays.asList("error1!", "error2!")));
        when(givenValidator2.validate(givenInput1)).thenReturn(
                new ValidationResult(true, Collections.emptyList()));
        when(givenValidator3.validate(givenInput2)).thenReturn(
                new ValidationResult(false, Collections.singletonList("error3!")));
        when(validatorRepository.find(GIVEN_VALIDATOR_1_NAME)).thenReturn(Optional.of(givenValidator1));
        when(validatorRepository.find(GIVEN_VALIDATOR_2_NAME)).thenReturn(Optional.of(givenValidator2));
        when(validatorRepository.find(GIVEN_VALIDATOR_3_NAME)).thenReturn(Optional.of(givenValidator3));

        // when
        final Collection<IndexedFailure> indexedFailures
                = validationService.validate(Arrays.asList(givenInput1, givenInput2));

        // then
        assertThat(indexedFailures, containsInAnyOrder(
                allOf(
                        hasProperty("index", equalTo(0)),
                        hasProperty("failures", containsInAnyOrder("error1!", "error2!"))
                ),
                allOf(
                        hasProperty("index", equalTo(1)),
                        hasProperty("failures", contains("error3!"))
                )));
    }

    @Test
    public void testValidate_shouldReturnCorrectResult_whenEmptyInputPassed() {
        // when
        final Collection<IndexedFailure> indexedFailures = validationService.validate(Collections.emptyList());

        // then
        assertThat(indexedFailures, emptyIterableOf(IndexedFailure.class));
    }

    @Test
    public void testValidate_shouldThrowAnException_whenInputWithNullElementsPassed() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("objects to validate can not contain nulls");

        // given
        final JSONObject givenInput1 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_1));
        final JSONObject givenInput2 = new JSONObject(Collections.singletonMap(TYPE_PROPERTY_NAME, GIVEN_TYPE_2));

        // when
        validationService.validate(Arrays.asList(givenInput1, null, givenInput2));

        // then expected exception
    }

    @Test
    public void testValidate_shouldThrowAnException_whenNullPassed() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("objects to validate can not be null");

        // when
        validationService.validate(null);

        // then expected exception
    }
}