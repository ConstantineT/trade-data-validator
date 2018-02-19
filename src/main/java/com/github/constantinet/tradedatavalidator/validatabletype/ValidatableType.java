package com.github.constantinet.tradedatavalidator.validatabletype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ValidatableType {

    private final String name;
    private final List<String> validatorNames;

    public ValidatableType(final String name, final Collection<String> validatorNames) {
        this.name = name;
        this.validatorNames = new ArrayList<>(validatorNames);
    }

    public String getName() {
        return name;
    }

    public Collection<String> getValidatorNames() {
        return Collections.unmodifiableList(validatorNames);
    }

    @Override
    public String toString() {
        return "validatabletype{" +
                "name='" + name + '\'' +
                ", validatorNames=" + validatorNames +
                '}';
    }
}