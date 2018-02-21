package com.github.constantinet.tradedatavalidator.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class IndexedFailure {

    private final int index;
    private final List<String> failures;

    @JsonCreator
    public IndexedFailure(@JsonProperty("index") final int index,
                          @JsonProperty("failures") final Collection<String> failures) {
        this.index = index;
        this.failures = new ArrayList<>(failures);
    }

    public int getIndex() {
        return index;
    }

    public Collection<String> getFailures() {
        return Collections.unmodifiableList(failures);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "index=" + index +
                ", failures=" + failures +
                '}';
    }
}