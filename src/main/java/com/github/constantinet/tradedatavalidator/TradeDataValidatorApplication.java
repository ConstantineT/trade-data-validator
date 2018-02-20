package com.github.constantinet.tradedatavalidator;

import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import com.github.constantinet.tradedatavalidator.validator.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
public class TradeDataValidatorApplication {

    public static final String TYPES_QUALIFIER = "types";
    public static final String VALIDATORS_QUALIFIER = "validators";

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        return messageSource;
    }

    @Bean
    @Qualifier(TYPES_QUALIFIER)
    public Collection<ValidatableType> types() {
        return Collections.emptyList();
    }

    @Bean
    @Qualifier(VALIDATORS_QUALIFIER)
    public Collection<Validator> validators() {
        return Collections.emptyList();
    }

    public static void main(final String[] args) {
        SpringApplication.run(TradeDataValidatorApplication.class, args);
    }
}