package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.CURRENCY_NOT_VALID_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrencyFormatValidatorTest {

    private final static String NAME = "testFormatValidator";

    private CurrencyFormatValidator formatValidator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        formatValidator = new CurrencyFormatValidator(NAME, messageConstructionStrategy);
    }

    @Test
    public void testFormatName_shouldReturnCorrectName() {
        // when
        final String name = formatValidator.formatName();

        // then
        assertThat(name, equalTo(NAME));
    }

    @Test
    public void testValidate_shouldReturnEmptyOptional_whenValidCurrencyCodeStringPassed() {
        // given
        final String givenCurrencyCode = "EUR";

        // when
        final Optional<String> result = formatValidator.validate(givenCurrencyCode);

        // then
        assertThat(result, hasProperty("present", equalTo(false)));
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenInvalidCurrencyCodeStringPassed() {
        testIncorrectInput("abc");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenEmptyCurrencyCodeStringPassed() {
        testIncorrectInput("");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenNullCurrencyCodeStringPassed() {
        testIncorrectInput(null);
    }

    private void testIncorrectInput(final String currencyCode) {
        // given
        when(messageConstructionStrategy.constructMessage(
                CURRENCY_NOT_VALID_KEY,
                NOT_VALID_MESSAGE,
                null)).thenReturn("error");
        // when
        final Optional<String> result = formatValidator.validate(currencyCode);

        // then
        assertThat(result, hasProperty("present", equalTo(true)));
        assertThat(result.get(), equalTo("error"));
    }
}