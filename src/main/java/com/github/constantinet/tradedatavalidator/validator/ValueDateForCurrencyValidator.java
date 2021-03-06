package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import com.github.constantinet.tradedatavalidator.validator.util.ValidatorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.github.constantinet.tradedatavalidator.Messages.DefaultMessages.NOT_VALID_MESSAGE;
import static com.github.constantinet.tradedatavalidator.Messages.Keys.*;
import static com.github.constantinet.tradedatavalidator.Properties.Formats.STANDARD_DATE_FORMATTER;
import static com.github.constantinet.tradedatavalidator.Properties.Names.CURRENCY_PAIR_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Names.VALUE_DATE_PROPERTY_NAME;

public class ValueDateForCurrencyValidator implements Validator {

    private static final String FIXER_URL = "http://api.fixer.io/{valueDate}?symbols={currency1},{currency2}";

    private static final Logger LOG = LoggerFactory.getLogger(ValueDateForCurrencyValidator.class);

    private final String name;
    private final RestTemplate restTemplate;
    private final MessageConstructionStrategy messageConstructionStrategy;

    public ValueDateForCurrencyValidator(final String name,
                                         final RestTemplate restTemplate,
                                         final MessageConstructionStrategy messageConstructionStrategy) {
        this.name = name;
        this.restTemplate = restTemplate;
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
        } catch (final JSONException | DateTimeParseException | RestClientException | IndexOutOfBoundsException ex) {
            LOG.warn("can not fulfill preconditions for checking value date against currency", ex);
            return new ValidationResult(false, Collections.singletonList(getCanNotValidateMessage()));
        }
    }

    private ValidationResult doValidate(final JSONObject object) {
        final Optional<Pair<String, LocalDate>> valueDatePair
                = ValidatorUtils.getDatePair(object, VALUE_DATE_PROPERTY_NAME, STANDARD_DATE_FORMATTER);

        final List<String> messages = new ArrayList<>();
        if (valueDatePair.isPresent()) {
            final String valueDateString = valueDatePair.get().getLeft();
            final LocalDate valueDate = valueDatePair.get().getRight();

            final String currencyPair = object.getString(CURRENCY_PAIR_PROPERTY_NAME);
            final String currency1 = currencyPair.substring(0, 3);
            final String currency2 = currencyPair.substring(3, 6);

            final LocalDate currencyDate1 = getDateForCurrencies(valueDateString, currency1, currency2);

            if (!valueDate.isEqual(currencyDate1)) {
                messages.add(getNotValidMessage(currencyPair));
            }
        }

        return new ValidationResult(messages.isEmpty(), messages);
    }

    private LocalDate getDateForCurrencies(final String valueDate, final String currency1, final String currency2) {
        final ResponseEntity<JSONObject> response = restTemplate.getForEntity(FIXER_URL,
                JSONObject.class, valueDate, currency1, currency2);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RestClientException(
                    "communication for with has failed with " + response.getStatusCode());
        }
        final String currencyDateString = response.getBody().getString("date");
        return LocalDate.parse(currencyDateString, STANDARD_DATE_FORMATTER);
    }

    private String getNotValidMessage(final String currencies) {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_NOT_VALID_FOR_CURRENCIES_KEY,
                NOT_VALID_MESSAGE,
                new String[]{currencies},
                VALUE_DATE_PROPERTY_NAME);
    }

    private String getCanNotValidateMessage() {
        return messageConstructionStrategy.constructMessage(
                VALUE_DATE_VALIDATION_AGAINST_CURRENCIES_NOT_POSSIBLE_KEY,
                CAN_NOT_VALIDATE_KEY,
                null,
                VALUE_DATE_PROPERTY_NAME);
    }
}