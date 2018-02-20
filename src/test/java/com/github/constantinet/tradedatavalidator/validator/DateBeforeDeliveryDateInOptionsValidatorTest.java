package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DateBeforeDeliveryDateInOptionsValidatorTest {

    private final static String NAME = "testValidator";
    private final static String PROPERTY_NAME = "date";

    private DateBeforeDeliveryDateInOptionsValidator validator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        validator = new DateBeforeDeliveryDateInOptionsValidator(NAME, PROPERTY_NAME, messageConstructionStrategy);
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenDateIsBeforeDeliveryDatePassed() {
        testValidJson("{\"type\": \"VanillaOption\", \"date\": \"2016-01-02\", \"deliveryDate\": \"2016-01-03\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenDateIsEqualToDeliveryDatePassed() {
        testInvalidJson("{\"type\": \"VanillaOption\", \"date\": \"2016-01-02\", \"deliveryDate\": \"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenDateIsAfterDeliveryDatePassed() {
        testInvalidJson("{\"type\": \"VanillaOption\", \"date\": \"2016-01-02\", \"deliveryDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidDatePassed() {
        testMisformedJson("{\"type\": \"VanillaOption\", \"date\": \"abc\", \"deliveryDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoDatePassed() {
        testMisformedJson("{\"type\": \"VanillaOption\", \"deliveryDate\": \"2016-01-01\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidDeliveryDatePassed() {
        testMisformedJson("{\"type\": \"VanillaOption\", \"date\": \"2016-01-02\", \"deliveryDate\": \"abc\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoDeliveryDatePassed() {
        testMisformedJson("{\"type\": \"VanillaOption\", \"date\": \"2016-01-02\"}");
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenNotSupportedTypePassed() {
        testValidJson("{\"type\": \"Spot\"}");
    }

    @Test
    public void testValidate_shouldReturnFailure_whenNoTypePassed() {
        testMisformedJson("{\"date\": \"2016-01-02\", \"deliveryDate\": \"2016-01-03\"}");
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

    private void testInvalidJson(final String json) {
        // given
        when(messageConstructionStrategy.constructMessage(
                DATE_NOT_BEFORE_DELIVERY_DATE_IN_OPTIONS_KEY,
                NOT_VALID_MESSAGE,
                new String[]{PROPERTY_NAME},
                PROPERTY_NAME)).thenReturn("error");
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
                DATE_BEFORE_DELIVERY_DATE_VALIDATION_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                new String[]{PROPERTY_NAME},
                PROPERTY_NAME)).thenReturn("error");
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