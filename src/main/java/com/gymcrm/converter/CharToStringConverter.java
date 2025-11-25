package com.gymcrm.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CharToStringConverter implements AttributeConverter<char[], String> {

    @Override
    public String convertToDatabaseColumn(char[] attribute) {
        return (attribute == null) ? null : new String(attribute);
    }

    @Override
    public char[] convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : dbData.toCharArray();
    }
}
