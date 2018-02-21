package com.github.constantinet.tradedatavalidator;

public final class ComponentNames {

    public static final String DEFAULT_MESSAGE_STRATEGY_QUALIFIER = "defaultMessageConstructionStrategy";
    public static final String PLAIN_RESOURCE_BUNDLE_MESSAGE_STRATEGY_QUALIFIER = "plainResourceBundleMessageStrategy";
    public static final String FIXED_APPLICATION_CLOCK_QUALIFIER = "fixedApplicationClock";

    public static final String CURRENCY_FORMAT_NAME = "currency";
    public static final String CURRENCY_PAIR_FORMAT_NAME = "currency-pair";
    public static final String DATE_FORMAT_NAME = "date";

    public static final String SPOT_FORWARD_SCHEMA_VALIDATOR_NAME = "spotForwardSchemaValidator";
    public static final String OPTIONS_SCHEMA_VALIDATOR_NAME = "optionsSchemaValidator";
    public static final String VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATOR_NAME = "valueDateNotBeforeTradeDateValidatorName";
    public static final String VALUE_DATE_FOR_CURRENCY_VALIDATOR_NAME = "valueDateForCurrencyValidator";
    public static final String VALUE_DATE_AGAINST_PRODUCT_TYPE_VALIDATOR_NAME = "valueDateAgainstProductTypeValidator";
    public static final String EXPIRY_DATE_BEFORE_DELIVERY_DATE_VALIDATOR_NAME = "expiryDateBeforeDeliveryDateInOptionsValidator";
    public static final String PREMIUM_DATE_BEFORE_DELIVERY_DATE_VALIDATOR_NAME = "premiumDateBeforeDeliveryDateInOptionsValidator";
    public static final String EXCERCISE_START_DATE_FOR_AMERICAN_STYLE_VALIDATOR_NAME = "exerciseStartDateForAmericanStyleValidator";

    private ComponentNames() {
        throw new AssertionError("Can not instantiate");
    }
}