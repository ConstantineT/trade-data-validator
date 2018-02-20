package com.github.constantinet.tradedatavalidator.message;

import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
class DefaultMessageConstructionStrategy implements MessageConstructionStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageConstructionStrategy.class);

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private final MessageSource messageSource;

    @Autowired
    public DefaultMessageConstructionStrategy(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String constructMessage(final String key,
                                   final String defaultMessage,
                                   final Object[] parameters,
                                   final String... pathValues) {
        Objects.requireNonNull(key, "message key can not be null");

        String message;
        try {
            // locale could also be taken from LocaleContextHolder
            message = messageSource.getMessage(key, parameters, DEFAULT_LOCALE);
        } catch (final NoSuchMessageException ex) {
            LOG.warn("can not retrieve a message by key " + key, ex);
            message = defaultMessage;
        }

        final ValidationException validationException = new ValidationException(null, message, null, null);
        if (pathValues != null) {
            Arrays.stream(pathValues)
                    .collect(Collectors.toCollection(ArrayDeque::new))
                    .descendingIterator()
                    .forEachRemaining(validationException::prepend);
        }
        return validationException.getMessage();
    }
}