package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import com.github.constantinet.tradedatavalidator.validator.util.ValidatorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

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
            return doValidate(object);
        } catch (final JSONException | DateTimeParseException ex) {
            LOG.warn("can not fulfill preconditions for checking if value date is before trade date", ex);
            return new ValidationResult(false, Collections.singletonList(getCanNotValidateMessage()));
        }
    }

    private ValidationResult doValidate(final JSONObject object) {
        final Optional<Pair<String, LocalDate>> valueDatePair
                = ValidatorUtils.getDatePair(object, VALUE_DATE_PROPERTY_NAME, STANDARD_DATE_FORMATTER);

        final LocalDate tradeDate
                = LocalDate.parse(object.getString(TRADE_DATE_PROPERTY_NAME), STANDARD_DATE_FORMATTER);

        final List<String> messages = new ArrayList<>();
        if (valueDatePair.isPresent() && valueDatePair.get().getRight().isBefore(tradeDate)) {
            messages.add(getNotValidMessage());
        }

        return new ValidationResult(messages.isEmpty(), messages);
    }

    private String getNotValidMessage() {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_BEFORE_TRADE_DATE_KEY,
                NOT_VALID_MESSAGE,
                null,
                VALUE_DATE_PROPERTY_NAME);
    }

    private String getCanNotValidateMessage() {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATION_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                null,
                VALUE_DATE_PROPERTY_NAME);
    }
}