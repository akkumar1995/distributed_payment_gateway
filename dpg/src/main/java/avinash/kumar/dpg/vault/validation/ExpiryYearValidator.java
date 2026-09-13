package avinash.kumar.dpg.vault.validation;

import jakarta.validation.ConstraintValidator;

import java.time.Year;

public class ExpiryYearValidator implements ConstraintValidator<ExpiryYear, Integer> {

    @Override
    public boolean isValid(Integer inputYear, jakarta.validation.ConstraintValidatorContext context) {

        if (inputYear == null) {
            return false;
        }

        int currentYear = Year.now().getValue();

        return inputYear >= currentYear;
    }
}
