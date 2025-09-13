package com.cymark.estatemanagementsystem.util;

import com.cymark.estatemanagementsystem.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;


public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            // Let @NotBlank handle null values - return true here
            return true;
        }

        List<String> errors = UserValidationUtils.getPasswordErrors(password);
        if (errors.isEmpty()) {
            return true;
        }

        // Custom error message
        context.disableDefaultConstraintViolation();
        String errorMessage = String.join(" ", errors);
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();

        return false;
    }
}