package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
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
import static com.github.constantinet.tradedatavalidator.Properties.Names.*;
import static com.github.constantinet.tradedatavalidator.Properties.Values.STYLE_AMERICAN_VALUE;
import static com.github.constantinet.tradedatavalidator.Properties.Values.TYPE_OPTIONS_VALUE;

public class ExerciseStartDateForAmericanStyleValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ExerciseStartDateForAmericanStyleValidator.class);

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ExerciseStartDateForAmericanStyleValidator(final String name,
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
            final String type = object.getString(TYPE_PROPERTY_NAME);

            final List<String> messages;
            if (TYPE_OPTIONS_VALUE.equals(type)) {
                messages = validateDate(object);
            } else {
                LOG.warn("can not validate exercise start date for type {0}", type);
                messages = Collections.emptyList();
            }

            return new ValidationResult(messages.isEmpty(), messages);
        } catch (final JSONException | DateTimeParseException ex) {
            LOG.warn("can not fulfill preconditions for checking validity of exercise start date", ex);
            return new ValidationResult(false, Collections.singletonList(getCanNotValidateMessage()));
        }
    }

    private List<String> validateDate(final JSONObject object) {
        final String style = object.getString(STYLE_PROPERTY_NAME);

        final List<String> messages = new ArrayList<>();
        if (STYLE_AMERICAN_VALUE.equals(style)) {
            final String exerciseStartDateString = object.getString(EXERCISE_START_DATE_PROPERTY_NAME);
            final LocalDate exerciseStartDate = LocalDate.parse(exerciseStartDateString, STANDARD_DATE_FORMATTER);

            final String tradeDateString = object.getString(TRADE_DATE_PROPERTY_NAME);
            final LocalDate tradeDate = LocalDate.parse(tradeDateString, STANDARD_DATE_FORMATTER);

            final String expiryDateString = object.getString(EXPIRY_DATE_PROPERTY_NAME);
            final LocalDate expiryDate = LocalDate.parse(expiryDateString, STANDARD_DATE_FORMATTER);

            if (!exerciseStartDate.isAfter(tradeDate)) {
                messages.add(getNotValidMessage(EXERCISE_START_DATE_NOT_AFTER_TRADE_DATE_KEY));
            }

            if (!exerciseStartDate.isBefore(expiryDate)) {
                messages.add(getNotValidMessage(EXERCISE_START_DATE_NOT_BEFORE_EXPIRY_DATE_KEY));
            }
        }

        return messages;
    }

    private String getNotValidMessage(final String key) {
        return messageConstructionStrategy.constructMessage(
                key,
                NOT_VALID_MESSAGE,
                Collections.singletonList(EXERCISE_START_DATE_PROPERTY_NAME));
    }

    private String getCanNotValidateMessage() {
        return messageConstructionStrategy.constructMessage(
                EXERCISE_START_DATE_VALIDATION_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                Collections.singletonList(EXERCISE_START_DATE_PROPERTY_NAME));
    }
}