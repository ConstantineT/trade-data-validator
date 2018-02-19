package com.github.constantinet.tradedatavalidator.message;

import java.util.List;

public interface MessageConstructionStrategy {

    String constructMessage(String key, String defaultMessage, List<String> pathValues, Object... parameters);
}