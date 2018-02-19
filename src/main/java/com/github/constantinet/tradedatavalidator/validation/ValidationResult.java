package com.github.constantinet.tradedatavalidator.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ValidationResult {

    private final boolean succeeded;
    private final List<String> failures;

    public ValidationResult(final boolean succeeded, final Collection<String> failures) {
        this.succeeded = succeeded;
        this.failures = new ArrayList<>(failures);
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public Collection<String> getFailures() {
        return Collections.unmodifiableList(failures);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "succeeded=" + succeeded +
                ", failures=" + failures +
                '}';
    }
}