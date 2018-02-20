package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Names.VALUE_DATE_PROPERTY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValueDateForCurrencyValidatorTest {

    private final static String NAME = "testValidator";

    private ValueDateForCurrencyValidator validator;
    private RestTemplate restTemplate;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        restTemplate = mock(RestTemplate.class);
        validator = new ValueDateForCurrencyValidator(NAME, restTemplate, messageConstructionStrategy);
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenValueDateIsValidForCurrencyPair() {
        // given
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("EUR")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-02\"}")));
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("USD")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-02\"}")));
        final JSONObject givenObject = new JSONObject("{\"ccyPair\": \"EURUSD\", \"valueDate\": \"2016-01-02\"}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(true)),
                hasProperty("failures", emptyIterable()))
        );
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsNotValidForCurrency1() {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_NOT_VALID_FOR_CURRENCY_KEY,
                NOT_VALID_MESSAGE,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME),
                "EUR")).thenReturn("error");
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("EUR")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-01\"}")));
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("USD")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-02\"}")));
        final JSONObject givenObject = new JSONObject("{\"ccyPair\": \"EURUSD\", \"valueDate\": \"2016-01-02\"}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error")))
        );
    }

    @Test
    public void testValidate_shouldReturnFailure_whenValueDateIsNotValidForCurrency2() {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_NOT_VALID_FOR_CURRENCY_KEY,
                NOT_VALID_MESSAGE,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME),
                "USD")).thenReturn("error");
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("EUR")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-02\"}")));
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("USD")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-01\"}")));
        final JSONObject givenObject = new JSONObject("{\"ccyPair\": \"EURUSD\", \"valueDate\": \"2016-01-02\"}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error")))
        );
    }

    @Test
    public void testValidate_shouldReturnFailure_whenCommunicationFailedCurrency1() {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_VALIDATION_AGAINST_CURRENCY_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME))).thenReturn("error");
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("EUR")))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("USD")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-02\"}")));
        final JSONObject givenObject = new JSONObject("{\"ccyPair\": \"EURUSD\", \"valueDate\": \"2016-01-02\"}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error")))
        );
    }

    @Test
    public void testValidate_shouldReturnFailure_whenCommunicationFailedCurrency2() {
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_VALIDATION_AGAINST_CURRENCY_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME))).thenReturn("error");
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("EUR")))
                .thenReturn(ResponseEntity.ok(new JSONObject("{\"date\": \"2016-01-02\"}")));
        when(restTemplate.getForEntity(anyString(), eq(JSONObject.class), eq("2016-01-02"), eq("USD")))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        final JSONObject givenObject = new JSONObject("{\"ccyPair\": \"EURUSD\", \"valueDate\": \"2016-01-02\"}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains("error")))
        );
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidValueDatePassed() {
        testMisformedJson("{\"ccyPair\": \"EURUSD\", \"valueDate\": \"abc\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoValueDatePassed() {
        testMisformedJson("{\"ccyPair\": \"EURUSD\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidCcyPairPassed() {
        testMisformedJson("{\"ccyPair\": \"\", \"valueDate\": \"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoCcyPairPassed() {
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

    private void testMisformedJson(final String json) {
        // given
        when(messageConstructionStrategy.constructMessage(
                VALUE_DATE_VALIDATION_AGAINST_CURRENCY_NOT_POSSIBLE_KEY,
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