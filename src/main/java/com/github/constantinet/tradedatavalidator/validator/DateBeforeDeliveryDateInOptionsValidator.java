package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Formats.STANDARD_DATE_FORMATTER;
import static com.github.constantinet.tradedatavalidator.Properties.Names.DELIVERY_DATE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Names.TYPE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Values.TYPE_OPTIONS_VALUE;

public class DateBeforeDeliveryDateInOptionsValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(DateBeforeDeliveryDateInOptionsValidator.class);

    private final String name;
    private final String propertyNameToValidate;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public DateBeforeDeliveryDateInOptionsValidator(final String name,
                                                    final String propertyNameToValidate,
                                                    final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = name;
        this.propertyNameToValidate = propertyNameToValidate;
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
            final String type = object.getString(TYPE_PROPERTY_NAME);

            final List<String> messages;
            if (TYPE_OPTIONS_VALUE.equals(type)) {
                messages = validateDate(object);
            } else {
                LOG.warn("can not validate if {0} is before premium date for type {1}", propertyNameToValidate, type);
                messages = Collections.emptyList();
            }

            return new ValidationResult(messages.isEmpty(), messages);
        } catch (final JSONException | DateTimeParseException ex) {
            LOG.warn("can not fulfill preconditions for checking if premium date is before delivery date", ex);
            return new ValidationResult(false, Collections.singletonList(getCanNotValidateMessage()));
        }
    }

    private List<String> validateDate(final JSONObject value) {
        final String dateString = value.getString(propertyNameToValidate);
        final LocalDate date = LocalDate.parse(dateString, STANDARD_DATE_FORMATTER);

        final String deliveryDateString = value.getString(DELIVERY_DATE_PROPERTY_NAME);
        final LocalDate deliveryDate = LocalDate.parse(deliveryDateString, STANDARD_DATE_FORMATTER);

        return date.isBefore(deliveryDate) ? Collections.emptyList() : Collections.singletonList(getNotValidMessage());
    }

    private String getNotValidMessage() {
        return messageConstructionStrategy.constructMessage(
                DATE_NOT_BEFORE_DELIVERY_DATE_IN_OPTIONS_KEY,
                NOT_VALID_MESSAGE,
                new String[]{propertyNameToValidate},
                propertyNameToValidate);
    }

    private String getCanNotValidateMessage() {
        return messageConstructionStrategy.constructMessage(
                DATE_BEFORE_DELIVERY_DATE_VALIDATION_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                new String[]{propertyNameToValidate},
                propertyNameToValidate);
    }
}