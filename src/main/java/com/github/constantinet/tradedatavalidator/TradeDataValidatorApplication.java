package com.github.constantinet.tradedatavalidator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.constantinet.tradedatavalidator.formatvalidator.CurrencyFormatValidator;
import com.github.constantinet.tradedatavalidator.formatvalidator.CurrencyPairFormatValidator;
import com.github.constantinet.tradedatavalidator.formatvalidator.DateFormatValidator;
import com.github.constantinet.tradedatavalidator.message.MessageConstructionStrategy;
import com.github.constantinet.tradedatavalidator.validatabletype.ValidatableType;
import com.github.constantinet.tradedatavalidator.validator.*;
import org.everit.json.schema.FormatValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.github.constantinet.tradedatavalidator.ComponentNames.*;
import static com.github.constantinet.tradedatavalidator.Properties.Names.EXPIRY_DATE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Names.PREMIUM_DATE_PROPERTY_NAME;
import static com.github.constantinet.tradedatavalidator.Properties.Values.*;

@SpringBootApplication
public class TradeDataValidatorApplication {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        final SimpleModule module = new SimpleModule("org.json");
        module.addSerializer(JSONObject.class, new JsonSerializer<JSONObject>() {
            @Override
            public void serialize(final JSONObject value,
                                  final JsonGenerator jgen,
                                  final SerializerProvider provider) throws IOException {
                jgen.writeRawValue(value.toString());
            }
        });
        module.addDeserializer(JSONObject.class, new JsonDeserializer<JSONObject>() {
            @Override
            public JSONObject deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
                final Map<String, Object> map = jp.readValueAs(new TypeReference<Map<String, Object>>() {});
                return new JSONObject(map);
            }
        });

        mapper.registerModule(module);

        return mapper;
    }

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        return messageSource;
    }

    @Bean
    public RestTemplate restTemplate(final ObjectMapper objectMapper) {
        final RestTemplate restTemplate = new RestTemplate();

        final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(objectMapper);

        restTemplate.getMessageConverters()
                .removeIf(mc -> MappingJackson2HttpMessageConverter.class.equals(mc.getClass()));
        restTemplate.getMessageConverters().add(messageConverter);

        return restTemplate;
    }

    @Bean
    public List<FormatValidator> formatValidators(@Qualifier(PLAIN_RESOURCE_BUNDLE_MESSAGE_STRATEGY_QUALIFIER)
                                                      final MessageConstructionStrategy messageStrategy) {
        return Arrays.asList(
                new CurrencyFormatValidator(CURRENCY_FORMAT_NAME, messageStrategy),
                new CurrencyPairFormatValidator(CURRENCY_PAIR_FORMAT_NAME, messageStrategy),
                new DateFormatValidator(DATE_FORMAT_NAME, messageStrategy)
        );
    }

    @Bean
    public Collection<ValidatableType> types() {
        final List<String> spotForwardValidatorNameList = Arrays.asList(
                SPOT_FORWARD_SCHEMA_VALIDATOR_NAME,
                VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATOR_NAME,
                VALUE_DATE_FOR_CURRENCY_VALIDATOR_NAME,
                VALUE_DATE_AGAINST_PRODUCT_TYPE_VALIDATOR_NAME
        );
        final List<String> optionsValidatorNameList = Arrays.asList(
                OPTIONS_SCHEMA_VALIDATOR_NAME,
                VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATOR_NAME,
                VALUE_DATE_FOR_CURRENCY_VALIDATOR_NAME,
                EXPIRY_DATE_BEFORE_DELIVERY_DATE_VALIDATOR_NAME,
                PREMIUM_DATE_BEFORE_DELIVERY_DATE_VALIDATOR_NAME,
                EXCERCISE_START_DATE_FOR_AMERICAN_STYLE_VALIDATOR_NAME
        );
        return Arrays.asList(
                new ValidatableType(TYPE_SPOT_VALUE, spotForwardValidatorNameList),
                new ValidatableType(TYPE_FORWARD_VALUE, spotForwardValidatorNameList),
                new ValidatableType(TYPE_OPTIONS_VALUE, optionsValidatorNameList)
        );
    }

    @Bean
    public Collection<Validator> validators(final List<FormatValidator> formatValidators,
                                            final RestTemplate restTemplate,
                                            @Qualifier(FIXED_APPLICATION_CLOCK_QUALIFIER)
                                                final Clock applicationClock,
                                            @Qualifier(DEFAULT_MESSAGE_STRATEGY_QUALIFIER)
                                                final MessageConstructionStrategy messageStrategy) throws IOException {
        return Arrays.asList(
                new SchemaValidator(SPOT_FORWARD_SCHEMA_VALIDATOR_NAME,
                        readJSONObject("schemas/SpotForwardSchema.json"), formatValidators),
                new SchemaValidator(OPTIONS_SCHEMA_VALIDATOR_NAME,
                        readJSONObject("schemas/OptionsSchema.json"), formatValidators),
                new ValueDateNotBeforeTradeDateValidator(VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATOR_NAME,
                        messageStrategy),
                new ValueDateForCurrencyValidator(VALUE_DATE_FOR_CURRENCY_VALIDATOR_NAME,
                        restTemplate, messageStrategy),
                new ValueDateAgainstProductTypeValidator(VALUE_DATE_AGAINST_PRODUCT_TYPE_VALIDATOR_NAME,
                        applicationClock, messageStrategy),
                new DateBeforeDeliveryDateInOptionsValidator(EXPIRY_DATE_BEFORE_DELIVERY_DATE_VALIDATOR_NAME,
                        EXPIRY_DATE_PROPERTY_NAME, messageStrategy),
                new DateBeforeDeliveryDateInOptionsValidator(PREMIUM_DATE_BEFORE_DELIVERY_DATE_VALIDATOR_NAME,
                        PREMIUM_DATE_PROPERTY_NAME, messageStrategy),
                new ExerciseStartDateForAmericanStyleValidator(EXCERCISE_START_DATE_FOR_AMERICAN_STYLE_VALIDATOR_NAME,
                        messageStrategy)
        );
    }

    @Bean(name = FIXED_APPLICATION_CLOCK_QUALIFIER)
    public Clock applicationClock() {
        final Instant timestamp = LocalDate.of(2016, Month.OCTOBER, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
        return Clock.fixed(timestamp, ZoneOffset.UTC);
    }

    private JSONObject readJSONObject(final String schemaLocation) throws IOException {
        final InputStream inputStream = new BufferedInputStream(new ClassPathResource(schemaLocation).getInputStream());
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int result = inputStream.read();
        while (result != -1) {
            outputStream.write((byte) result);
            result = inputStream.read();
        }
        return new JSONObject(outputStream.toString("UTF-8"));
    }

    public static void main(final String[] args) {
        SpringApplication.run(TradeDataValidatorApplication.class, args);
    }
}