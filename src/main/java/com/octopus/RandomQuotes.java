package com.octopus;

import com.amazonaws.services.lambda.runtime.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomQuotes {
    private final AtomicInteger count = new AtomicInteger(0);

    public ProxyResponse handleRequest(final Map<String,Object> input, final Context context) {
        try {
            final List<String> authors = load("/authors.txt");
            final List<String> quotes = load("/quotes.txt");
            final int randomIndex = new Random().nextInt(authors.size());

            final String json = "{\"quote\": \"" + quotes.get(randomIndex) + "\", " +
                    "\"author\": \"" + authors.get(randomIndex) + "\", " +
                    "\"appVersion\": \"" + getVersion() + "\", " +
                    "\"environmentName\": \"Google Cloud Functions\", " +
                    "\"quoteCount\": \"" + count.getAndIncrement() + "\" " +
                    "}";

            return new ProxyResponse("200", json);
        } catch (Exception ex) {
            return new ProxyResponse("500", ex.toString());
        }
    }

    private List<String> load(final String path) {
        try (
                final InputStream inputStream = this.getClass().getResourceAsStream(path);
                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                final Stream<String> lines = bufferedReader.lines()
        ) {
            return lines.collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of("");
        }
    }

    private String getVersion() {
        try {
            final InputStream resourceAsStream = this.getClass().getResourceAsStream(
                    "/META-INF/maven/com.octopus/randomquotesapi-lambda/pom.properties");
            final Properties props = new Properties();
            props.load(resourceAsStream);
            return props.get("version").toString();
        } catch (final Exception e) {
            return "unknown";
        }
    }
}