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
import static com.github.constantinet.tradedatavalidator.Properties.Names.VALUE_DATE_PROPERTY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueDateNotBeforeTradeDateValidatorTest {

    private final static String NAME = "testValidator";

    private ValueDateNotBeforeTradeDateValidator validator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        validator = new ValueDateNotBeforeTradeDateValidator(NAME, messageConstructionStrategy);
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenValueDateIsEqualToTradeDatePassed() {
        testValidJson("{\"valueDate\": \"2016-01-02\", \"tradeDate\": \"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenValueDateIsAfterTradeDatePassed() {
        testValidJson("{\"valueDate\": \"2016-01-02\", \"tradeDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsBeforeTradeDatePassed() {
        testInvalidJson("{\"valueDate\": \"2016-01-02\", \"tradeDate\": \"2016-01-03\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidValueDatePassed() {
        testMisformedJson("{\"valueDate\": \"abc\", \"tradeDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoValueDatePassed() {
        testMisformedJson("{\"tradeDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidTradeDatePassed() {
        testMisformedJson("{\"valueDate\": \"2016-01-02\", \"tradeDate\": \"abc\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoTradeDatePassed() {
        testMisformedJson("{\"valueDate\": \"2016-01-02\"}");
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

    private void testInvalidJson(final String json) {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_BEFORE_TRADE_DATE_KEY,
                NOT_VALID_MESSAGE,
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

    private void testMisformedJson(final String json) {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATION_NOT_POSSIBLE_KEY,
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