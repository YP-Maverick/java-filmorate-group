package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FilmControllerTest {
    private FilmController filmController;
    private Validator validator;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createFilm() {
        final List<Film> emptyFilms = filmController.findAll();
        assertEquals(0, emptyFilms.size(), "Вернулся не пустой список фильмов.");

        film = Film.builder().id(1)
                .name("film_name").description("film_descr")
                .releaseDate(LocalDate.parse("2000-02-02"))
                .duration(90)
                .build();

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);

        assertEquals( 0, constraintViolations.size());

        final Film newFilm = filmController.create(film);
        final List<Film> films = filmController.findAll();

        assertNotNull(newFilm, "Фильм не создан.");
        assertEquals(film, newFilm, "Фильмы не соответствуют.");
        assertEquals(1, films.size(), "В списке фильмов нет созданного фильма.");
    }

    @Test
    public void validateEmptyOrNullName() {
        film = Film.builder().id(1)
                .name("")
                .description("film_descr").releaseDate(LocalDate.parse("2000-02-02")).duration(90).build();

        Set<ConstraintViolation<Film>> blankViol = validator.validate(film);

        assertEquals( 1, blankViol.size());
        assertEquals( "не должно быть пустым", blankViol.iterator().next().getMessage());

        Film newFilm = Film.builder().id(2)
                .name(null)
                .description("film_descr").releaseDate(LocalDate.parse("2000-02-02")).duration(90).build();

        Set<ConstraintViolation<Film>> nullViol = validator.validate(newFilm);
        assertEquals( 1, nullViol.size());
        assertEquals( "не должно быть пустым", nullViol.iterator().next().getMessage());
    }
    @Test
    public void validateDescriptionSizeMore200() {
        film = Film.builder().id(1).name("film_name")
                .description("some description more then 200, some description more then 200, some description more"
                        + " then 200, some description more then 200, some description more then 200, some description"
                        + " more then 200, some description more then 200")
                .releaseDate(LocalDate.parse("2000-02-02")).duration(90).build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);

        assertEquals( 1, violation.size());
        assertEquals( "размер должен находиться в диапазоне от 0 до 200", violation.iterator().next().getMessage());
    }

    @Test
    public void validateReleaseDate() {
        film = Film.builder().id(1).name("film_name").description("film_descr")
                .releaseDate(LocalDate.parse("1895-12-28"))
                .duration(90).build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);

        assertEquals( 0, violation.size());

        final Film newFilm = Film.builder().id(2).name("film_name").description("film_descr")
                .releaseDate(LocalDate.parse("1895-12-27"))
                .duration(90).build();

        Set<ConstraintViolation<Film>> afterViol = validator.validate(newFilm);

        assertEquals( 1, afterViol.size());
        assertEquals( "Дата релиза должна быть не раньше 28 декабря 1895 года.", afterViol.iterator().next().getMessage());
    }

    @Test
    public void validateNegativeDuration() {
        film = Film.builder().id(1).name("film_name").description("film_descr").releaseDate(LocalDate.parse("2000-02-02"))
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violation = validator.validate(film);

        assertEquals( 1, violation.size());
        assertEquals( "должно быть больше 0", violation.iterator().next().getMessage());

        final Film newFilm = Film.builder().id(2).name("film_name").description("film_descr").releaseDate(LocalDate.parse("2000-02-02"))
                .duration(-1)
                .build();

        Set<ConstraintViolation<Film>> negativeViol = validator.validate(newFilm);

        assertEquals( 1, negativeViol.size());
        assertEquals( "должно быть больше 0", negativeViol.iterator().next().getMessage());
    }
}
