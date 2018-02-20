package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.everit.json.schema.FormatValidator;

import java.util.Currency;
import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.CURRENCY_NOT_VALID_KEY;

public class CurrencyFormatValidator implements FormatValidator {

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public CurrencyFormatValidator(final String name, final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = name;
        this.messageConstructionStrategy = messageConstructionStrategy;
    }

    @Override
    public Optional<String> validate(final String string) {
        final boolean valid = Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .anyMatch(code -> code.equals(string));
        return valid ? Optional.empty() : getFailure();
    }

    @Override
    public String formatName() {
        return name;
    }

    private Optional<String> getFailure() {
        return Optional.of(messageConstructionStrategy.constructMessage(
                CURRENCY_NOT_VALID_KEY, NOT_VALID_MESSAGE, null));
    }
}