package com.github.constantinet.tradedatavalidator.validation.controller;

import com.github.constantinet.tradedatavalidator.validation.IndexedFailure;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testValidate_shouldReturn200AndNotEmptyResponse_whenNotValidInputPassed() throws Exception {
        // given
        final HttpEntity<List<JSONObject>> entity = buildHttpEntityFromFile("notValidInput.json");

        // when
        final ResponseEntity<Collection<IndexedFailure>> response = restTemplate.exchange(
                "http://localhost:{port}/trades/validation",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Collection<IndexedFailure>>() {
                },
                port);

        // then
        assertThat(response, allOf(
                hasProperty("statusCode", equalTo(HttpStatus.OK)),
                hasProperty("body", contains(
                        getIndexedFailureAllOfMatcher(0,
                                "#/customer: PLUTO3 is not a valid enum value",
                                "#/ccyPair: not a valid pair of currency codes",
                                "#/direction: abc is not a valid enum value",
                                "#/amount1: 1000000.001 is not a multiple of 0.01",
                                "#/amount2: 1120000.001 is not a multiple of 0.01",
                                "#/rate: 1.121 is not a multiple of 0.01",
                                "#/valueDate: value date is before trade data",
                                "#/legalEntity: Invalid is not a valid enum value",
                                "#: required key [trader] not found"
                        ),
                        getIndexedFailureAllOfMatcher(1,
                                "#/valueDate: value date is not equal to date of trade for currencies EURUSD"),
                        getIndexedFailureAllOfMatcher(3,
                                "#: trade type is not supported"),
                        getIndexedFailureAllOfMatcher(4,
                                "#/customer: PLUTO3 is not a valid enum value",
                                "#/ccyPair: not a valid pair of currency codes",
                                "#: required key [strategy] not found",
                                "#/direction: abc is not a valid enum value",
                                "#/amount1: 1000000.001 is not a multiple of 0.01",
                                "#/amount2: 1120000.001 is not a multiple of 0.01",
                                "#/rate: 1.121 is not a multiple of 0.01",
                                "#: required key [premiumType] not found",
                                "#/premium: 0.201 is not a multiple of 0.01",
                                "#/payCcy: not a valid currency code",
                                "#/premiumCcy: not a valid currency code",
                                "#/legalEntity: Invalid is not a valid enum value",
                                "#: required key [trader] not found"),
                        getIndexedFailureAllOfMatcher(5,
                                "#/excerciseStartDate: exercise start date is not after trade date",
                                "#/excerciseStartDate: exercise start date is not before expiry date")
                ))
        ));
    }

    @Test
    public void testValidate_shouldReturn200AndEmptyResponse_whenValidInputPassed() throws Exception {
        // given
        final HttpEntity<List<JSONObject>> entity = buildHttpEntityFromFile("validInput.json");

        // when
        final ResponseEntity<Collection<IndexedFailure>> response = restTemplate.exchange(
                "http://localhost:{port}/trades/validation",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Collection<IndexedFailure>>() {
                },
                port);

        // then
        assertThat(response, allOf(
                hasProperty("statusCode", equalTo(HttpStatus.OK)),
                hasProperty("body", emptyIterableOf(IndexedFailure.class))
        ));
    }

    @Test
    public void testValidate_shouldReturn200AndEmptyResponse_whenEmptyInputPassed() throws Exception {
        // given
        final HttpEntity<List<JSONObject>> entity = new HttpEntity<>(new ArrayList<>());

        // when
        final ResponseEntity<Collection<IndexedFailure>> response = restTemplate.exchange(
                "http://localhost:{port}/trades/validation",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Collection<IndexedFailure>>() {
                },
                port);

        // then
        assertThat(response, allOf(
                hasProperty("statusCode", equalTo(HttpStatus.OK)),
                hasProperty("body", emptyIterableOf(IndexedFailure.class))
        ));
    }

    private Matcher<Object> getIndexedFailureAllOfMatcher(final int index, final String... failureFragments) {
        final List<Matcher<String>> matchers = Arrays.stream(failureFragments)
                .map(Matchers::containsString)
                .collect(Collectors.toList());
        return allOf(
                hasProperty("index", equalTo(index)),
                hasProperty("failures", containsInAnyOrder(matchers.toArray(new Matcher[matchers.size()])))
        );
    }

    private HttpEntity<List<JSONObject>> buildHttpEntityFromFile(final String location) throws IOException {
        final InputStream inputStream = new BufferedInputStream(new ClassPathResource(location).getInputStream());
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int result = inputStream.read();
        while (result != -1) {
            outputStream.write((byte) result);
            result = inputStream.read();
        }
        final List<JSONObject> body = new JSONArray(outputStream.toString("UTF-8")).toList().stream()
                .map(obj -> (Map<String, Object>) obj)
                .map(JSONObject::new)
                .collect(Collectors.toList());

        return new HttpEntity<>(body);
    }
}