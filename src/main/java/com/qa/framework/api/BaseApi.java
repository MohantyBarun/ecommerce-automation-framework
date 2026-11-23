package com.qa.framework.api;

import com.qa.framework.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import static io.restassured.RestAssured.given;

/** Shared request spec: base URI, form encoding, Allure attachments and overload back-off. */
public abstract class BaseApi {

    private static final Logger LOG = LogManager.getLogger(BaseApi.class);
    private static final int OVERLOAD_RETRIES = 4;

    private static final RequestSpecification SPEC = new RequestSpecBuilder()
            .setBaseUri(ConfigManager.apiBaseUrl())
            .setContentType(ContentType.URLENC.withCharset("UTF-8"))
            .addFilter(new AllureRestAssured())
            .build();

    /**
     * Sends the request, retrying when the public site answers with its HTML
     * "under heavy load" page instead of JSON.
     */
    protected ApiResponse send(Function<RequestSpecification, Response> call) {
        Response response = call.apply(given().spec(SPEC));
        for (int attempt = 1; attempt <= OVERLOAD_RETRIES && isOverloaded(response); attempt++) {
            LOG.warn("API overloaded, retry {}/{}", attempt, OVERLOAD_RETRIES);
            sleep(attempt * 3_000L);
            response = call.apply(given().spec(SPEC));
        }
        return ApiResponse.from(response);
    }

    private static boolean isOverloaded(Response response) {
        return response.statusCode() >= 500 || response.asString().contains("under heavy load");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
