package com.qa.framework.models;

/** A shopper account. Field values are valid for both the signup form and the createAccount API. */
public record User(
        String name,
        String email,
        String password,
        String title,        // "Mr" or "Mrs"
        String birthDay,     // "1".."31"
        String birthMonth,   // "1".."12"
        String birthYear,    // "1900".."2021"
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,      // must match a signup dropdown option
        String state,
        String city,
        String zipcode,
        String mobileNumber) {

    @Override
    public String toString() {
        return "User[" + email + "]";
    }
}
