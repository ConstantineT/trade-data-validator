package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Names.VALUE_DATE_PROPERTY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueDateAgainstProductTypeValidatorTest {

    private final static String NAME = "testValidator";
    // 2016-01-02
    private final static Clock CLOCK = Clock.fixed(Instant.ofEpochMilli(1451692800000L), ZoneId.of("UTC"));

    private ValueDateAgainstProductTypeValidator validator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        validator = new ValueDateAgainstProductTypeValidator(NAME, CLOCK, messageConstructionStrategy);
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenValueDateIsBeforeCurrentForSpot() {
        testValidJson("{\"type\": \"Spot\", \"valueDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenValueDateIAfterCurrentForForward() {
        testValidJson("{\"type\": \"Forward\", \"valueDate\": \"2016-01-03\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsEqualToCurrentForSpot() {
        testInvalidJson("{\"type\": \"Spot\", \"valueDate\": \"2016-01-02\"}",
                VALUE_DATE_NOT_BEFORE_CURRENT_IN_SPOT_TRADE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsAfterCurrentForSpot() {
        testInvalidJson("{\"type\": \"Spot\", \"valueDate\": \"2016-01-03\"}",
                VALUE_DATE_NOT_BEFORE_CURRENT_IN_SPOT_TRADE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsEqualToCurrentForForward() {
        testInvalidJson("{\"type\": \"Forward\", \"valueDate\": \"2016-01-02\"}",
                VALUE_DATE_NOT_AFTER_CURRENT_IN_FORWARD_TRADE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsBeforeCurrentForForward() {
        testInvalidJson("{\"type\": \"Forward\", \"valueDate\": \"2016-01-01\"}",
                VALUE_DATE_NOT_AFTER_CURRENT_IN_FORWARD_TRADE_KEY);
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidValueDatePassed() {
        testMisformedJson("{\"type\": \"Spot\", \"valueDate\": \"abc\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoValueDatePassed() {
        testMisformedJson("{\"type\": \"Spot\"}");
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenNotSupportedTypePassed() {
        testValidJson("{\"type\": \"VanillaOption\", \"valueDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoTypePassed() {
        testMisformedJson("{\"valueDate\": \"2016-01-01\"}");
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
                hasProperty("failures", emptyIterable()))
        );
    }

    private void testInvalidJson(final String json, final String key) {
        // given
        when(messageConstructionStrategy.constructMessage(
                key,
                NOT_VALID_MESSAGE,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME))).thenReturn("error");
        final JSONObject givenObject = new JSONObject(json);

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error"))
        ));
    }

    private void testMisformedJson(final String json) {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_AGAINST_PRODUCT_TYPE_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME))).thenReturn("error");
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