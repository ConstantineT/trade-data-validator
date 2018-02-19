package com.github.constantinet.tradedatavalidator;

public final class Messages {

    public static final class Keys {
        public static final String TYPE_NOT_SUPPORTED_KEY = "message.typeNotSupported";
        public static final String CAN_NOT_VALIDATE_KEY = "message.validationNotPossible";

        public Keys() {
            throw new AssertionError("Can not instantiate");
        }
    }

    public static final class DefaultMessages {
        public static final String TYPE_NOT_SUPPORTED_MESSAGE = "type not supported";
        public static final String CAN_NOT_VALIDATE_MESSAGE = "can not validate";

        public DefaultMessages() {
            throw new AssertionError("Can not instantiate");
        }
    }

    public Messages() {
        throw new AssertionError("Can not instantiate");
    }
}