package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IsAfterValidator.class)
public @interface IsAfter {
    String message() default "Дата должна быть не раньше назначенной даты.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String date() default "1895-12-27";
}
