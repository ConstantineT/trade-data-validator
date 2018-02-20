package com.github.constantinet.tradedatavalidator.formatvalidator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.FormatValidator;

import java.util.Currency;
import java.util.Optional;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.CURRENCY_PAIR_NOT_VALID_KEY;

public class CurrencyPairFormatValidator implements FormatValidator {

    private final String name;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public CurrencyPairFormatValidator(final String name, final MessageConstructionStrategy
            messageConstructionStrategy) {
        this.name = name;
        this.messageConstructionStrategy = messageConstructionStrategy;
    }

    @Override
    public Optional<String> validate(final String string) {
        if (StringUtils.length(string) != 6) {
            return getFailure();
        }

        final String currency1 = string.substring(0, 3);
        final String currency2 = string.substring(3, 6);

        return (validCurrency(currency1) && validCurrency(currency2)) ? Optional.empty() : getFailure();
    }

    @Override
    public String formatName() {
        return name;
    }

    private boolean validCurrency(final String string) {
        return Currency.getAvailableCurrencies().stream()
                .map(Currency::getCurrencyCode)
                .anyMatch(code -> code.equals(string));
    }

    private Optional<String> getFailure() {
        return Optional.of(messageConstructionStrategy.constructMessage(
                CURRENCY_PAIR_NOT_VALID_KEY, NOT_VALID_MESSAGE, null));
    }
}