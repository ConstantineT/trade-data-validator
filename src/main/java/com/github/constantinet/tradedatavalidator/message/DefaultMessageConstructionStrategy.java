package com.github.constantinet.tradedatavalidator.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class DefaultMessageConstructionStrategy implements MessageConstructionStrategy {

    private final MessageSource messageSource;

    @Autowired
    public DefaultMessageConstructionStrategy(final MessageSource messageSource) {
        this.messageSource = null;
    }

    @Override
    public String constructMessage(final String key,
                                   final String defaultMessage,
                                   final List<String> pathValues,
                                   final Object... parameters) {
        return null;
    }
}