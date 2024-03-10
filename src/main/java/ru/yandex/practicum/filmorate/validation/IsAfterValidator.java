package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class IsAfterValidator implements ConstraintValidator<IsAfter, LocalDate> {

    private LocalDate minDate;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.date());
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintContext) {
        if (date == null) {
            return false;
        } else return date.isAfter(minDate);
    }
}
