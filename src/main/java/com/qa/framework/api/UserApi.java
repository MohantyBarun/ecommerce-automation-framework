package com.qa.framework.api;

import com.qa.framework.models.User;
import io.qameta.allure.Step;

import java.util.LinkedHashMap;
import java.util.Map;

/** Account endpoints of the automationexercise.com public API. */
public class UserApi extends BaseApi {

    @Step("API: create account {user}")
    public ApiResponse createAccount(User user) {
        return send(req -> req.formParams(toForm(user)).post("/createAccount"));
    }

    @Step("API: verify login for {email}")
    public ApiResponse verifyLogin(String email, String password) {
        return send(req -> req.formParam("email", email).formParam("password", password).post("/verifyLogin"));
    }

    @Step("API: get account details for {email}")
    public ApiResponse getUserDetailByEmail(String email) {
        return send(req -> req.queryParam("email", email).get("/getUserDetailByEmail"));
    }

    @Step("API: delete account {email}")
    public ApiResponse deleteAccount(String email, String password) {
        return send(req -> req.formParam("email", email).formParam("password", password).delete("/deleteAccount"));
    }

    private static Map<String, String> toForm(User u) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("name", u.name());
        form.put("email", u.email());
        form.put("password", u.password());
        form.put("title", u.title());
        form.put("birth_date", u.birthDay());
        form.put("birth_month", u.birthMonth());
        form.put("birth_year", u.birthYear());
        form.put("firstname", u.firstName());
        form.put("lastname", u.lastName());
        form.put("company", u.company());
        form.put("address1", u.address1());
        form.put("address2", u.address2());
        form.put("country", u.country());
        form.put("zipcode", u.zipcode());
        form.put("state", u.state());
        form.put("city", u.city());
        form.put("mobile_number", u.mobileNumber());
        return form;
    }
}
