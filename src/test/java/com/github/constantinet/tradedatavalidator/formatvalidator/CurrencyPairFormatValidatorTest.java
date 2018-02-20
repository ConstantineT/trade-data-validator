package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.CURRENCY_PAIR_NOT_VALID_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrencyPairFormatValidatorTest {

    private final static String NAME = "testFormatValidator";

    private CurrencyPairFormatValidator formatValidator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        formatValidator = new CurrencyPairFormatValidator(NAME, messageConstructionStrategy);
    }

    @Test
    public void testFormatName_shouldReturnCorrectName() {
        // when
        final String name = formatValidator.formatName();

        // then
        assertThat(name, equalTo(NAME));
    }

    @Test
    public void testValidate_shouldReturnEmptyOptional_whenValidCurrencyPairStringPassed() {
        // given
        final String givenCurrencyPair = "EURUSD";

        // when
        final Optional<String> result = formatValidator.validate(givenCurrencyPair);

        // then
        assertThat(result, hasProperty("present", equalTo(false)));
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenInvalidCurrencyPairStringPassed() {
        testIncorrectInput("abcdef");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenCurrencyPairStringWithInvalidFirstCurrencyPassed() {
        testIncorrectInput("abcUSD");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenCurrencyPairStringWithInvalidSecondCurrencyPassed() {
        testIncorrectInput("EURdef");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenShortCurrencyPairStringPassed() {
        testIncorrectInput("EUR");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenEmptyCurrencyPairStringPassed() {
        testIncorrectInput("");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenNullCurrencyPairStringPassed() {
        testIncorrectInput(null);
    }

    private void testIncorrectInput(final String currencyPair) {
        // given
        when(messageConstructionStrategy.constructMessage(
                CURRENCY_PAIR_NOT_VALID_KEY,
                NOT_VALID_MESSAGE,
                null)).thenReturn("error");
        // when
        final Optional<String> result = formatValidator.validate(currencyPair);

        // then
        assertThat(result, hasProperty("present", equalTo(true)));
        assertThat(result.get(), equalTo("error"));
    }
}