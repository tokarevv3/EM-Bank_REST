package com.example.bankcards.util.validation.validator;

import com.example.bankcards.entity.Status;
import com.example.bankcards.util.validation.annotation.AllowedStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class AllowedStatusValidator implements ConstraintValidator<AllowedStatus, Status> {

    private static final Set<Status> allowed = Set.of(Status.ACTIVE, Status.BLOCKED);

    @Override
    public boolean isValid(Status value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return allowed.contains(value);
    }
}

