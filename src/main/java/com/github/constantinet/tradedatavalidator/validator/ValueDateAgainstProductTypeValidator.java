package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Formats.STANDARD_DATE_FORMATTER;
import static com.github.constantinet.tradedatavalidator.Properties.Names.TYPE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Names.VALUE_DATE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Values.TYPE_FORWARD_VALUE;
import static com.github.constantinet.tradedatavalidator.Properties.Values.TYPE_SPOT_VALUE;

public class ValueDateAgainstProductTypeValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ValueDateAgainstProductTypeValidator.class);

    private final String name;
    private final Clock clock;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ValueDateAgainstProductTypeValidator(final String name,
                                                final Clock clock,
                                                final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = name;
        this.clock = clock;
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
            final LocalDate current = clock.instant().atOffset(ZoneOffset.UTC).toLocalDate();

            final String valueDateString = object.getString(VALUE_DATE_PROPERTY_NAME);
            final LocalDate valueDate = LocalDate.parse(valueDateString, STANDARD_DATE_FORMATTER);

            final String type = object.getString(TYPE_PROPERTY_NAME);

            final List<String> messages = new ArrayList<>();

            if (TYPE_SPOT_VALUE.equals(type) && !valueDate.isBefore(current)) {
                messages.add(getNotValidMessage(VALUE_DATE_NOT_BEFORE_CURRENT_IN_SPOT_TRADE_KEY));
            } else if (TYPE_FORWARD_VALUE.equals(type) && !valueDate.isAfter(current)) {
                messages.add(getNotValidMessage(VALUE_DATE_NOT_AFTER_CURRENT_IN_FORWARD_TRADE_KEY));
            } else {
                LOG.warn("can not validate valueDate against type {0}", type);
            }

            return new ValidationResult(messages.isEmpty(), messages);
        } catch (final JSONException | DateTimeParseException ex) {
            LOG.warn("can not fulfill preconditions for checking value date against product type", ex);
            return new ValidationResult(false, Collections.singletonList(getCanNotValidateMessage()));
        }
    }

    private String getNotValidMessage(final String key) {
        return messageConstructionStrategy.constructMessage(
                key,
                NOT_VALID_MESSAGE,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME));
    }

    private String getCanNotValidateMessage() {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_AGAINST_PRODUCT_TYPE_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(VALUE_DATE_PROPERTY_NAME));
    }
}