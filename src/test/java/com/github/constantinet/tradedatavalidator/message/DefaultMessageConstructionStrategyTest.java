package com.github.constantinet.tradedatavalidator.message;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMessageConstructionStrategyTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DefaultMessageConstructionStrategy messageConstructionStrategy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConstructMessage_shouldReturnCorrectMessage_whenValidKeyPassed() {
        // given
        final String givenKey = "test.error";
        final String givenMessage = "error!";
        final String givenDefaultMessage = "errorDefault!";
        when(messageSource.getMessage(eq(givenKey), any(), any())).thenReturn(givenMessage);

        // when
        final String message = messageConstructionStrategy
                .constructMessage(givenKey, givenDefaultMessage, null);

        // then
        assertThat(message, equalTo("#: " + givenMessage));
    }

    @Test
    public void testConstructMessage_shouldReturnCorrectMessage_whenValidKeyAndPathValuesPassed() {
        // given
        final String givenKey = "test.error";
        final String givenMessage = "error!";
        final String givenDefaultMessage = "errorDefault!";
        when(messageSource.getMessage(eq(givenKey), any(), any())).thenReturn(givenMessage);

        // when
        final String message = messageConstructionStrategy
                .constructMessage(givenKey, givenDefaultMessage, null, "a", "b");

        // then
        assertThat(message, equalTo("#/a/b: " + givenMessage));
    }

    @Test
    public void testConstructMessage_shouldReturnCorrectMessage_whenInvalidKeyPassed() {
        // given
        final String givenKey = "test.error";
        final String givenDefaultMessage = "errorDefault!";
        when(messageSource.getMessage(eq(givenKey), any(), any())).thenThrow(new NoSuchMessageException("test"));

        // when
        final String message = messageConstructionStrategy
                .constructMessage(givenKey, givenDefaultMessage, null);

        // then
        assertThat(message, equalTo("#: " + givenDefaultMessage));
    }

    @Test
    public void testConstructMessage_shouldThrowException_whenNullKeyPassed() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("message key can not be null");

        // when
        messageConstructionStrategy.constructMessage(null, "errorDefault!", null);

        // then expected exception
    }
}