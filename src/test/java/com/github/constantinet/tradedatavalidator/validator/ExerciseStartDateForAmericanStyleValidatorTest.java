package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Names.EXERCISE_START_DATE_PROPERTY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExerciseStartDateForAmericanStyleValidatorTest {

    private final static String NAME = "testValidator";

    private ExerciseStartDateForAmericanStyleValidator validator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        validator = new ExerciseStartDateForAmericanStyleValidator(NAME, messageConstructionStrategy);
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenCorrectExerciseStartDatePassed() {
        testValidJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"2016-01-02\", \"tradeDate\":\"2016-01-01\", \"expiryDate\":\"2016-01-03\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenExerciseStartDateEqualToTradeDatePassed() {
        testInvalidJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                        "\"excerciseStartDate\":\"2016-01-01\", \"tradeDate\":\"2016-01-01\", " +
                        "\"expiryDate\":\"2016-01-03\"}",
                EXERCISE_START_DATE_NOT_AFTER_TRADE_DATE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenExerciseStartBeforeTradeDatePassed() {
        testInvalidJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                        "\"excerciseStartDate\":\"2015-12-31\", \"tradeDate\":\"2016-01-01\", " +
                        "\"expiryDate\":\"2016-01-03\"}",
                EXERCISE_START_DATE_NOT_AFTER_TRADE_DATE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenExerciseStartDateEqualToExpiryDatePassed() {
        testInvalidJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                        "\"excerciseStartDate\":\"2016-01-02\", \"tradeDate\":\"2016-01-01\", " +
                        "\"expiryDate\":\"2016-01-02\"}",
                EXERCISE_START_DATE_NOT_BEFORE_EXPIRY_DATE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenExerciseStartDateAfterExpiryDatePassed() {
        testInvalidJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                        "\"excerciseStartDate\":\"2016-01-03\", \"tradeDate\":\"2016-01-01\", " +
                        "\"expiryDate\":\"2016-01-02\"}",
                EXERCISE_START_DATE_NOT_BEFORE_EXPIRY_DATE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidExerciseStartDatePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"abc\", \"tradeDate\":\"2016-01-01\", \"expiryDate\":\"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoStartDatePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"tradeDate\":\"2016-01-01\", \"expiryDate\":\"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidTradeDatePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"2016-01-01\", \"tradeDate\":\"abc\", \"expiryDate\":\"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoTradeDatePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"2016-01-01\", \"expiryDate\":\"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidExpiryDatePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"2016-01-02\", \"tradeDate\":\"2016-01-01\", \"expiryDate\":\"abc\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoExpiryDatePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", \"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"2016-01-02\", \"tradeDate\":\"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenNotSupportedTypePassed() {
        testValidJson("{\"type\": \"Spot\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoTypePassed() {
        testMisformedJson("{\"style\":\"AMERICAN\", " +
                "\"excerciseStartDate\":\"2016-01-02\", \"tradeDate\":\"2016-01-01\", \"expiryDate\":\"2016-01-03\"}");
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenNotSupportedStylePassed() {
        testValidJson("{\"type\":\"VanillaOption\", \"style\":\"EUROPEAN\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoStylePassed() {
        testMisformedJson("{\"type\":\"VanillaOption\", " +
                "\"excerciseStartDate\":\"2016-01-02\", \"tradeDate\":\"2016-01-01\", \"expiryDate\":\"2016-01-03\"}");
    }

    @Test
    public void testValidate_shouldThrowException_whenNullPassed() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("object can not be null");

        // when
        validator.validate(null);

        // then expected exception
    }

    private void testValidJson(final String json) {
        // given
        final JSONObject givenObject = new JSONObject(json);

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(true)),
                hasProperty("failures", emptyIterable())
        ));
    }

    private void testInvalidJson(final String json, final String key) {
        // given
        when(messageConstructionStrategy.constructMessage(
                key,
                NOT_VALID_MESSAGE,
                Collections.singletonList(EXERCISE_START_DATE_PROPERTY_NAME))).thenReturn("error");
        final JSONObject givenObject = new JSONObject(json);

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error")))
        );
    }

    private void testMisformedJson(final String json) {
        // given
        when(messageConstructionStrategy.constructMessage(
                EXERCISE_START_DATE_VALIDATION_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(EXERCISE_START_DATE_PROPERTY_NAME))).thenReturn("error");
        final JSONObject givenObject = new JSONObject(json);
        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error")))
        );
    }
}