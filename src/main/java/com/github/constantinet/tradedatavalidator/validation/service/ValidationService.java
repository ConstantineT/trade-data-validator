package com.github.constantinet.tradedatavalidator.validation.service;

import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public interface ValidationService {

    Collection<IndexedFailure> validate(final List<JSONObject> objects);
}