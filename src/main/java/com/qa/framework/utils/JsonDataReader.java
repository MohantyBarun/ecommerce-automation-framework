package com.qa.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/** Reads JSON test data from the classpath and turns it into TestNG DataProvider rows. */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
    }

    public static <T> List<T> readList(String resource, Class<T> type) {
        try (InputStream in = JsonDataReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("Test data not found on classpath: " + resource);
            }
            CollectionType listType = MAPPER.getTypeFactory().constructCollectionType(List.class, type);
            return MAPPER.readValue(in, listType);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse " + resource, e);
        }
    }

    /** One DataProvider row per JSON array element. */
    public static <T> Object[][] toDataProvider(String resource, Class<T> type) {
        return readList(resource, type).stream()
                .map(item -> new Object[]{item})
                .toArray(Object[][]::new);
    }
}
