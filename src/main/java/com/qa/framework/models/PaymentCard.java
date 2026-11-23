package com.qa.framework.models;

public record PaymentCard(String nameOnCard, String number, String cvc, String expiryMonth, String expiryYear) {
}
