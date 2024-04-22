package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.adapter.DateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    FilmService filmService;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new DateAdapter()).create();
    private final String path = "/films";
    private Validator validator;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createFilm() throws Exception {
        film = Film.builder()
                .name("film_name").description("film_descr")
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(90)
                .mpa(RatingMpa.builder()
                        .id(1)
                        .build())
                .build();

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);

        assertEquals(0, constraintViolations.size());

        mvc.perform(post(path)
                        .content(gson.toJson(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        content().json(gson.toJson(film)));
    }

    @Test
    public void getEmptyListOfFilms() throws Exception {
        final List<Film> emptyFilms = new ArrayList<>();

        mvc.perform(get(path).contentType(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk(),
                content().json(gson.toJson(emptyFilms)));
    }

    @Test
    public void validateEmptyName() throws Exception {
        film = Film.builder()
                .id(1L)
                .name("")
                .description("film_descr")
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(90)
                .build();

        Set<ConstraintViolation<Film>> blankViol = validator.validate(film);

        assertEquals(1, blankViol.size());
        assertEquals("Название фильма не должно быть пустым", blankViol.iterator().next().getMessage());

        mvc.perform(post(path).content(gson.toJson(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateNullName() throws Exception {
        film = Film.builder()
                .id(1L)
                .name(null)
                .description("film_descr")
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(90)
                .build();

        Set<ConstraintViolation<Film>> nullViol = validator.validate(film);
        assertEquals(1, nullViol.size());
        assertEquals("Название фильма не должно быть пустым", nullViol.iterator().next().getMessage());

        mvc.perform(post(path).content(gson.toJson(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateDescriptionSizeMore200() throws Exception {
        film = Film.builder()
                .id(1L)
                .name("film_name")
                .description("1".repeat(201))
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(90)
                .build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);

        assertEquals(1, violation.size());
        assertEquals("Размер описания должен быть не больше 200 символов", violation.iterator().next().getMessage());

        mvc.perform(post(path).content(gson.toJson(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateReleaseDate() throws Exception {
        film = Film.builder()
                .id(1L)
                .name("film_name")
                .description("film_descr")
                .releaseDate(LocalDate.parse("1895-12-28"))
                .duration(90)
                .build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);

        assertEquals(0, violation.size());

        final Film newFilm = Film.builder()
                .id(2L)
                .name("film_name")
                .description("film_descr")
                .releaseDate(LocalDate.parse("1895-12-27"))
                .duration(90)
                .build();

        Set<ConstraintViolation<Film>> afterViol = validator.validate(newFilm);

        assertEquals(1, afterViol.size());
        assertEquals("Дата должна быть не раньше назначенной даты.", afterViol.iterator().next().getMessage());

        mvc.perform(post(path).content(gson.toJson(newFilm)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateNullDuration() throws Exception {
        film = Film.builder()
                .id(1L)
                .name("film_name")
                .description("film_descr")
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);

        assertEquals(1, violation.size());
        assertEquals("Продолжительность фильма должна быть больше 0", violation.iterator().next().getMessage());

        mvc.perform(post(path).content(gson.toJson(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void validateNegativeDuration() throws Exception {
        film = Film.builder()
                .id(1L)
                .name("film_name")
                .description("film_descr")
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(-1)
                .build();

        Set<ConstraintViolation<Film>> negativeViol = validator.validate(film);

        assertEquals(1, negativeViol.size());
        assertEquals("Продолжительность фильма должна быть больше 0", negativeViol.iterator().next().getMessage());

        mvc.perform(post(path).content(gson.toJson(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isBadRequest());
    }
}
