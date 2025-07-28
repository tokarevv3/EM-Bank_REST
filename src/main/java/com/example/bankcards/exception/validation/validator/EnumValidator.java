package com.example.bankcards.exception.validation.validator;

import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.validation.annotation.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumValue, Role> {

    private Set<String> acceptedValues;

    @Override
    public void initialize(EnumValue annotation) {
        acceptedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Role value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return acceptedValues.contains(value.name());
    }
}

