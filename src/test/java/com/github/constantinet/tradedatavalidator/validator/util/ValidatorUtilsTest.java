package com.github.constantinet.tradedatavalidator.validator.util;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

public class ValidatorUtilsTest {

    private static final String PROPERTY_NAME = "date";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetDatePair_shouldReturnCorrectPair_whenValidDataPassed() {
        // given
        final JSONObject givenObject = new JSONObject("{\"" + PROPERTY_NAME + "\": \"2016-01-02\"}");

        // when
        final Optional<Pair<String, LocalDate>> datePair
                = ValidatorUtils.getDatePair(givenObject, PROPERTY_NAME, DATE_TIME_FORMATTER);

        // then
        assertThat(datePair, notNullValue());
        assertThat(datePair.isPresent(), equalTo(true));
        assertThat(datePair.get(), allOf(
                hasProperty("left", equalTo("2016-01-02")),
                hasProperty("right", equalTo(LocalDate.of(2016, Month.JANUARY, 2)))
        ));
    }

    @Test
    public void testGetDatePair_shouldReturnCorrectPair_whenObjectWithNoPropertyPassed() {
        // given
        final JSONObject givenObject = new JSONObject("{\"date1\": \"2016-01-02\"}");

        // when
        final Optional<Pair<String, LocalDate>> datePair
                = ValidatorUtils.getDatePair(givenObject, PROPERTY_NAME, DATE_TIME_FORMATTER);

        // then
        assertThat(datePair, notNullValue());
        assertThat(datePair.isPresent(), equalTo(false));
    }

    @Test
    public void testGetDatePair_shouldThrowException_whenObjectWithInvalidPropertyPassed() {
        // given
        expectedException.expect(DateTimeParseException.class);
        final JSONObject givenObject = new JSONObject("{\"date\": \"2016-13-02\"}");

        // when
        ValidatorUtils.getDatePair(givenObject, PROPERTY_NAME, DATE_TIME_FORMATTER);

        // then expected exception
    }

    @Test
    public void testGetDatePair_shouldThrowException_wheNullObjectPassed() {
        // given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("object can not be null");

        // when
        ValidatorUtils.getDatePair(null, PROPERTY_NAME, DATE_TIME_FORMATTER);

        // then expected exception
    }

    @Test
    public void testGetDatePair_shouldThrowException_wheNullDatePropertyNamePassed() {
        // given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("datePropertyName can not be null");

        // when
        ValidatorUtils.getDatePair(new JSONObject("{\"date\": \"2016-01-02\"}"), null, DATE_TIME_FORMATTER);

        // then expected exception
    }

    @Test
    public void testGetDatePair_shouldThrowException_wheNullDateFormatterPassed() {
        // given
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("dateFormatter can not be null");

        // when
        ValidatorUtils.getDatePair(new JSONObject("{\"date\": \"2016-01-02\"}"), PROPERTY_NAME, null);

        // then expected exception
    }
}