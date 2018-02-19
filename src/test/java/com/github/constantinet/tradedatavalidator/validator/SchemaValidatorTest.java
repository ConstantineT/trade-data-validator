package com.github.constantinet.tradedatavalidator.validator;

import com.github.constantinet.tradedatavalidator.validation.ValidationResult;
import org.everit.json.schema.FormatValidator;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

public class SchemaValidatorTest {

    private final static String NAME = "testValidator";

    private static JSONObject schema;
    private static FormatValidator formatValidator;

    private SchemaValidator validator;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
        schema = new JSONObject("{\n" +
                "  \"$schema\": \"http://json-schema.org/draft-06/schema#\",\n" +
                "  \"title\": \"Test Schema\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"name\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"format\": \"not-empty-string\"\n" +
                "    },\n" +
                "    \"description\": {\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  }\n" +
                "}");
        formatValidator = new FormatValidator() {
            @Override
            public String formatName() {
                return "not-empty-string";
            }

            @Override
            public Optional<String> validate(final String s) {
                return s.isEmpty() ? Optional.of("error") : Optional.empty();
            }
        };
    }

    @Before
    public void setUp() {
        validator = new SchemaValidator(NAME, schema, Collections.singletonList(formatValidator));
    }

    @Test
    public void testValidate_shouldReturnSuccess_whenValidInputPassed() {
        // given
        final JSONObject givenObject = new JSONObject("{\"name\": \"test\", \"description\": \"test\"}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(true)),
                hasProperty("failures", emptyIterable())
        ));
    }

    @Test
    public void testValidate_shouldReturnFailure_whenInvalidInputPassed() {
        // given
        final JSONObject givenObject = new JSONObject("{\"name\": \"\", \"description\": 3}");

        // when
        final ValidationResult result = validator.validate(givenObject);

        // then
        assertThat(result, allOf(
                hasProperty("succeeded", equalTo(false)),
                hasProperty("failures", contains(equalTo("#/name: error"), containsString("#/description")))
        ));
    }

    @Test
    public void testValidate_shouldThrowException_whenNullPassed() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("object can not be null");

        // when
        validator.validate(null);

        // then expected exception
    }

}