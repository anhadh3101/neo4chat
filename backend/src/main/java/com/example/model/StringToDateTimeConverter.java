package com.example.model;

import org.neo4j.driver.Values;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WritingConverter
public class StringToDateTimeConverter implements Converter<String, org.neo4j.driver.Value> {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public org.neo4j.driver.Value convert(String source) {
        if (source == null || source.isEmpty()) {
            return Values.NULL;
        }
        
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(source, ISO_FORMATTER);
            return Values.value(zonedDateTime);
        } catch (Exception e) {
            // If parsing fails, store as string
            return Values.value(source);
        }
    }
}

