package com.github.constantinet.tradedatavalidator;

public final class Messages {

    public static final class Keys {
        public static final String TYPE_NOT_SUPPORTED_KEY = "message.typeNotSupported";
        public static final String CURRENCY_NOT_VALID_KEY = "message.currencyNotValid";
        public static final String CURRENCY_PAIR_NOT_VALID_KEY = "message.currencyPairNotValid";
        public static final String DATE_NOT_VALID_KEY = "message.dateNotValid";
        public static final String CAN_NOT_VALIDATE_KEY = "message.validationNotPossible";

        public static final String VALUE_DATE_NOT_VALID_FOR_CURRENCIES_KEY
                = "message.valueDateNotValidForCurrencies";
        public static final String VALUE_DATE_VALIDATION_AGAINST_CURRENCIES_NOT_POSSIBLE_KEY
                = "message.valueDateAgainstCurrenciesValidationNotPossible";

        public static final String VALUE_DATE_BEFORE_TRADE_DATE_KEY
                = "message.valueDateBeforeTradeDate";
        public static final String VALUE_DATE_NOT_BEFORE_TRADE_DATE_VALIDATION_NOT_POSSIBLE_KEY
                = "message.valueDateNotBeforeTradeDateValidationNotPossible";

        public static final String VALUE_DATE_NOT_BEFORE_CURRENT_IN_SPOT_TRADE_KEY
                = "message.valueDateNotBeforeCurrentSpotTrade";
        public static final String VALUE_DATE_NOT_AFTER_CURRENT_IN_FORWARD_TRADE_KEY
                = "message.valueDateNotAfterCurrentForwardTrade";
        public static final String VALUE_DATE_AGAINST_PRODUCT_TYPE_NOT_POSSIBLE_KEY
                = "message.valueDateAgainstProductTypeValidationNotPossible";

        public static final String DATE_NOT_BEFORE_DELIVERY_DATE_IN_OPTIONS_KEY
                = "message.dateNotBeforeDeliveryDateInOptions";
        public static final String DATE_BEFORE_DELIVERY_DATE_VALIDATION_NOT_POSSIBLE_KEY
                = "message.dateBeforeDeliveryDateValidationNotPossible";

        public static final String EXERCISE_START_DATE_NOT_AFTER_TRADE_DATE_KEY
                = "message.exerciseStartDateNotAfterTradeDateForAmericanOptions";
        public static final String EXERCISE_START_DATE_NOT_BEFORE_EXPIRY_DATE_KEY
                = "message.exerciseStartDateNotBeforeExpiryDateForAmericanOptions";
        public static final String EXERCISE_START_DATE_VALIDATION_NOT_POSSIBLE_KEY
                = "message.exerciseStartDateForAmericanOptionsValidationNotPossible";

        private Keys() {
            throw new AssertionError("Can not instantiate");
        }
    }

    public static final class DefaultMessages {
        public static final String TYPE_NOT_SUPPORTED_MESSAGE = "type not supported";
        public static final String CAN_NOT_VALIDATE_MESSAGE = "can not validate";
        public static final String NOT_VALID_MESSAGE = "not valid";

        private DefaultMessages() {
            throw new AssertionError("Can not instantiate");
        }
    }

    private Messages() {
        throw new AssertionError("Can not instantiate");
    }
}