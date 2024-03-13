package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.adapter.DateAdapter;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new DateAdapter()).create();
    private final String path = "/users";
    private Validator validator;
    private User user;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createUser() throws Exception {
        user = User.builder()
                .id(1)
                .email("user@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 1,1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());

        mvc.perform(post(path)
                .content(gson.toJson(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.email").value("user@ya.ru"),
                        jsonPath("$.login").value("login"),
                        jsonPath("$.name").value("name"),
                        jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    public void validateEmptyEmail() throws Exception {
        user = User.builder().id(1)
                .email("")
                .login("login").name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> blankViol = validator.validate(user);

        assertEquals(1, blankViol.size());
        assertEquals("Электронная почта не должна быть пустой", blankViol.iterator().next().getMessage());

        mvc.perform(post(path)
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateWrongEmail() throws Exception {
        user = User.builder().id(1)
                .email("123")
                .login("login").name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> emailViol = validator.validate(user);

        assertEquals(1, emailViol.size());
        assertEquals("Электронная почта должна иметь формат адреса электронной почты", emailViol.iterator().next().getMessage());

        mvc.perform(post(path)
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateNullLogin() throws Exception {
        user = User.builder().id(1).email("user@ya.ru")
                .login(null)
                .name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> blankViol = validator.validate(user);

        assertEquals(1, blankViol.size());
        assertEquals("Логин не должен быть пустым", blankViol.iterator().next().getMessage());
        mvc.perform(post(path)
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateWithSpaceLogin() throws Exception {
        user = User.builder().id(1).email("user@ya.ru")
                .login("1 2 3 ")
                .name("name").birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> loginViol = validator.validate(user);

        assertEquals(1, loginViol.size());
        assertEquals("Логин не должен содержать пробелы", loginViol.iterator().next().getMessage());
        mvc.perform(post(path)
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateBlankName() throws Exception {
        user = User.builder().id(1).email("user@ya.ru").login("login")
                .name("")
                .birthday(LocalDate.of(2000, 1,1)).build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());

        mvc.perform(post(path)
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.email").value("user@ya.ru"),
                        jsonPath("$.login").value("login"),
                        jsonPath("$.name").value("login"),
                        jsonPath("$.birthday").value("2000-01-01"));
    }

    @Test
    public void validateBirthday() throws Exception {
        user = User.builder().id(1).email("user@ya.ru").login("login")
                .name("")
                .birthday(LocalDate.of(2025, 1,1)).build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());

        mvc.perform(post(path)
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }
}
