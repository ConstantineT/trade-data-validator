package com.github.constantinet.tradedatavalidator;

import java.time.format.DateTimeFormatter;

public final class Properties {

    public static final class Names {
        public static final String TYPE_PROPERTY_NAME = "type";
        public static final String STYLE_PROPERTY_NAME = "style";
        public static final String VALUE_DATE_PROPERTY_NAME = "valueDate";
        public static final String TRADE_DATE_PROPERTY_NAME = "tradeDate";
        public static final String CURRENCY_PAIR_PROPERTY_NAME = "ccyPair";
        public static final String EXPIRY_DATE_PROPERTY_NAME = "expiryDate";
        public static final String PREMIUM_DATE_PROPERTY_NAME = "premiumDate";
        public static final String DELIVERY_DATE_PROPERTY_NAME = "deliveryDate";
        public static final String EXERCISE_START_DATE_PROPERTY_NAME = "excerciseStartDate";

        public Names() {
            throw new AssertionError("Can not instantiate");
        }
    }

    public static final class Formats {
        public static final DateTimeFormatter STANDARD_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        public Formats() {
            throw new AssertionError("Can not instantiate");
        }
    }

    public static final class Values {
        public static final String TYPE_SPOT_VALUE = "Spot";
        public static final String TYPE_FORWARD_VALUE = "Forward";
        public static final String TYPE_OPTIONS_VALUE = "VanillaOption";
        public static final String STYLE_AMERICAN_VALUE = "AMERICAN";

        public Values() {
            throw new AssertionError("Can not instantiate");
        }
    }

    public Properties() {
        throw new AssertionError("Can not instantiate");
    }
}