package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Formats.STANDARD_DATE_FORMATTER;
import static com.github.constantinet.tradedatavalidator.Properties.Names.TRADE_DATE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Names.VALUE_DATE_PROPERTY_NAME;

public class ValueDateNotBeforeTradeDateValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ValueDateNotBeforeTradeDateValidator.class);

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ValueDateNotBeforeTradeDateValidator(final String name,
                                                final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = name;
        this.messageConstructionStrategy = messageConstructionStrategy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ValidationResult validate(final JSONObject object) {
        Objects.requireNonNull(object, "object can not be null");

        try {
            final String valueDateString = object.getString(VALUE_DATE_PROPERTY_NAME);
            final LocalDate valueDate = LocalDate.parse(valueDateString, STANDARD_DATE_FORMATTER);

            final String tradeDateString = object.getString(TRADE_DATE_PROPERTY_NAME);
            final LocalDate tradeDate = LocalDate.parse(tradeDateString, STANDARD_DATE_FORMATTER);

            return valueDate.isBefore(tradeDate) ?
                    new ValidationResult(false, Collections.singletonList(getNotValidMessage())) :
                    new ValidationResult(true, Collections.emptyList());
        } catch (final JSONException | DateTimeParseException ex) {
            LOG.warn("can not fulfill preconditions for checking if value date is before trade date", ex);
            return new ValidationResult(false, Collections.singletonList(getCanNotValidateMessage()));
        }
    }

    private String getNotValidMessage() {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_BEFORE_TRADE_DATE_KEY,
                NOT_VALID_MESSAGE,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME));
    }

    private String getCanNotValidateMessage() {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATION_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME));
    }
}