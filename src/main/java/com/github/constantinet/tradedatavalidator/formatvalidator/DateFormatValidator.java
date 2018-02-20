package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.everit.json.schema.FormatValidator;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.DATE_NOT_VALID_KEY;
import static com.github.constantinet.tradedatavalidator.Properties.Formats.STANDARD_DATE_FORMATTER;

public class DateFormatValidator implements FormatValidator {

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public DateFormatValidator(final String name, final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = name;
        this.messageConstructionStrategy = messageConstructionStrategy;
    }

    @Override
    public Optional<String> validate(final String string) {
        if (string == null) {
            return getFailure();

        }

        try {
            LocalDate.parse(string, STANDARD_DATE_FORMATTER);
            return Optional.empty();
        } catch (final DateTimeParseException ex) {
            return getFailure();
        }
    }

    @Override
    public String formatName() {
        return name;
    }

    private Optional<String> getFailure() {
        return Optional.of(messageConstructionStrategy.constructMessage(
                DATE_NOT_VALID_KEY, NOT_VALID_MESSAGE, null));
    }
}