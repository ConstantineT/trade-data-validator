package com.github.constantinet.tradedatavalidator.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
class PlainResourceBundleMessageConstructionStrategy implements MessageConstructionStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PlainResourceBundleMessageConstructionStrategy.class);

    static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private final MessageSource messageSource;

    @Autowired
    public PlainResourceBundleMessageConstructionStrategy(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String constructMessage(final String key,
                                   final String defaultMessage,
                                   final Object[] parameters,
                                   final String... pathValues) {
        Objects.requireNonNull(key, "message key can not be null");

        try {
            // locale could also be taken from LocaleContextHolder
            return messageSource.getMessage(key, parameters, DEFAULT_LOCALE);
        } catch (final NoSuchMessageException ex) {
            LOG.warn("can not retrieve a message by key " + key, ex);
            return defaultMessage;
        }
    }
}