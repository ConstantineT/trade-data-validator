package com.github.constantinet.tradedatavalidator.message;

public interface MessageConstructionStrategy {

    String constructMessage(String key, String defaultMessage, Object[] parameters, String... pathValues);
}