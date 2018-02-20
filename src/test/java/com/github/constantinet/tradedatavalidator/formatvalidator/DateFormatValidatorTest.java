package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.DATE_NOT_VALID_KEY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DateFormatValidatorTest {

    private final static String NAME = "testFormatValidator";

    private DateFormatValidator formatValidator;
    private MessageConstructionStrategy messageConstructionStrategy;

    @Before
    public void setUp() {
        messageConstructionStrategy = mock(MessageConstructionStrategy.class);
        formatValidator = new DateFormatValidator(NAME, messageConstructionStrategy);
    }

    @Test
    public void testFormatName_shouldReturnCorrectName() {
        // when
        final String name = formatValidator.formatName();

        // then
        assertThat(name, equalTo(NAME));
    }

    @Test
    public void testValidate_shouldReturnEmptyOptional_whenValidDateStringPassed() {
        // given
        final String givenDate = "2018-02-28";

        // when
        final Optional<String> result = formatValidator.validate(givenDate);

        // then
        assertThat(result, hasProperty("present", equalTo(false)));
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenNotExistingDateStringPassed() {
        testIncorrectInput("2018-02-29");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenInvalidDateStringPassed() {
        testIncorrectInput("2018.02.28");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenEmptyDateStringPassed() {
        testIncorrectInput("");
    }

    @Test
    public void testValidate_shouldReturnNotEmptyOptional_whenNullDateStringPassed() {
        testIncorrectInput(null);
    }

    private void testIncorrectInput(final String date) {
        // given
        when(messageConstructionStrategy.constructMessage(
                DATE_NOT_VALID_KEY,
                NOT_VALID_MESSAGE,
                null)).thenReturn("error");
        // when
        final Optional<String> result = formatValidator.validate(date);

        // then
        assertThat(result, hasProperty("present", equalTo(true)));
        assertThat(result.get(), equalTo("error"));
    }
}