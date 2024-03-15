package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NotContainsSpacesValidator.class)
public @interface NotContainsSpaces {
    String message() default "не должен содержать пробелы";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
