package com.example.model;

import org.neo4j.driver.Value;
import org.neo4j.driver.types.Type;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ReadingConverter
public class DateTimeToStringConverter implements Converter<Value, String> {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String convert(Value source) {
        if (source == null || source.isNull()) {
            return null;
        }
        
        Type type = source.type();
        String typeName = type.name();
        
        // Check if it's a DATE_TIME type
        if ("DATE_TIME".equals(typeName) || "ZONED_DATE_TIME".equals(typeName)) {
            try {
                ZonedDateTime zonedDateTime = source.asZonedDateTime();
                return zonedDateTime.format(ISO_FORMATTER);
            } catch (Exception e) {
                // Fall through to string conversion
            }
        }
        
        // If it's already a string or other type, convert to string
        try {
            return source.asString();
        } catch (Exception e) {
            return null;
        }
    }
}

