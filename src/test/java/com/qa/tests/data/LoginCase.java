package com.qa.tests.data;

/** Row of testdata/invalid-logins.json. A null expectedError means browser-side validation should block the submit. */
public record LoginCase(String scenario, String email, String password, String expectedError) {

    @Override
    public String toString() {
        return scenario;
    }
}
