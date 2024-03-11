package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {
    private UserController userController;
    private Validator validator;
    private User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createUser() {
        final List<User> emptyUsers = userController.findAll();
        assertEquals(0, emptyUsers.size(), "Вернулся не пустой список пользователей.");

        user = User.builder()
                .id(1)
                .email("user@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals( 0, violations.size());

        final User newUser = userController.create(user);
        final List<User> users = userController.findAll();

        assertNotNull(newUser, "Пользователь не создан.");
        assertEquals(user, newUser, "Пользователи не соответствуют.");
        assertEquals(1, users.size(), "В списке пользователей нет созданного пользователя.");
    }

    @Test
    public void validateWrongEmail() {
        user = User.builder().id(1)
                .email("")
                .login("login").name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> blankViol = validator.validate(user);

        assertEquals( 1, blankViol.size());
        assertEquals( "не должно быть пустым", blankViol.iterator().next().getMessage());

        final User newUser = User.builder().id(2)
                .email("123")
                .login("login").name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> emailViol = validator.validate(newUser);

        assertEquals( 1, emailViol.size());
        assertEquals( "должно иметь формат адреса электронной почты", emailViol.iterator().next().getMessage());
    }

    @Test
    public void validateBlankLogin() {
        user = User.builder().id(1).email("user@ya.ru")
                .login(null)
                .name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> blankViol = validator.validate(user);

        assertEquals( 1, blankViol.size());
        assertEquals( "не должно быть пустым", blankViol.iterator().next().getMessage());

        final User newUser = User.builder().id(2).email("user@ya.ru")
                .login("1 2 3 ")
                .name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> loginViol = validator.validate(newUser);

        assertEquals( 1, loginViol.size());
        assertEquals( "не должен содержать пробелы", loginViol.iterator().next().getMessage());
    }

    @Test
    public void validateBlankName() {
        user = User.builder().id(1).email("user@ya.ru").login("login")
                .name("")
                .birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals( 0, violations.size());

        User newUser = userController.create(user);
        assertNotNull(newUser, "Пользователь не создан.");
        assertEquals(user.getLogin(), newUser.getName(), "Имя пользователя не соответствует логину.");



    }
}
