package com.qa.framework.utils;

import com.qa.framework.models.PaymentCard;
import com.qa.framework.models.User;
import net.datafaker.Faker;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Generates unique, realistic test data so parallel runs never collide. */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker();
    private static final List<String> COUNTRIES =
            List.of("India", "United States", "Canada", "Australia", "Israel", "New Zealand", "Singapore");

    private TestDataFactory() {
    }

    public static User newUser() {
        String first = FAKER.name().firstName();
        String last = FAKER.name().lastName();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new User(
                first + " " + last,
                "qa." + UUID.randomUUID().toString().substring(0, 12) + "@example.com",
                "Pass@" + rnd.nextInt(100_000, 999_999),
                rnd.nextBoolean() ? "Mr" : "Mrs",
                String.valueOf(rnd.nextInt(1, 29)),
                String.valueOf(rnd.nextInt(1, 13)),
                String.valueOf(rnd.nextInt(1970, 2004)),
                first,
                last,
                FAKER.company().name(),
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                COUNTRIES.get(rnd.nextInt(COUNTRIES.size())),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.address().zipCode(),
                FAKER.phoneNumber().cellPhone().replaceAll("[^0-9]", ""));
    }

    public static PaymentCard newCard(User owner) {
        return new PaymentCard(
                owner.firstName() + " " + owner.lastName(),
                "4111111111111111",
                String.valueOf(ThreadLocalRandom.current().nextInt(100, 999)),
                "12",
                "2030");
    }
}
