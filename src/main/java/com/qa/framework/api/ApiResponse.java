package com.qa.framework.api;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * automationexercise.com always answers HTTP 200 and puts the real status in a
 * "responseCode" field, served as text/html. This wrapper parses the body as JSON
 * regardless of content type and exposes both codes.
 */
public record ApiResponse(int httpStatus, JsonPath body, String raw) {

    public static ApiResponse from(Response response) {
        String raw = response.asString();
        return new ApiResponse(response.statusCode(), JsonPath.from(raw), raw);
    }

    public int responseCode() {
        return body.getInt("responseCode");
    }

    public String message() {
        return body.getString("message");
    }
}
