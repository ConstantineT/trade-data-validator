package com.github.constantinet.tradedatavalidator.validator.util;


import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

public final class ValidatorUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatorUtils.class);

    private ValidatorUtils() {
        throw new AssertionError("Can not instantiate");
    }

    public static Optional<Pair<String, LocalDate>> getDatePair(final JSONObject object,
                                                                final String datePropertyName,
                                                                final DateTimeFormatter dateFormatter)
            throws DateTimeParseException {
        Objects.requireNonNull(object, "object can not be null");
        Objects.requireNonNull(datePropertyName, "datePropertyName can not be null");
        Objects.requireNonNull(dateFormatter, "dateFormatter can not be null");

        try {
            final String dateString = object.getString(datePropertyName);
            final LocalDate date = LocalDate.parse(dateString, dateFormatter);
            return Optional.of(Pair.of(dateString, date));
        } catch (final JSONException ex) {
            LOG.info("can not validate missing " + datePropertyName, ex);
            return Optional.empty();
        }
    }
}