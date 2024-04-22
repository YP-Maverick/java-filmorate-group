package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.RatingMpaDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private static ModelMapper modelMapper;

    private Film createFilm() {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder()
                .id(5)
                .name("Документальный")
                .build());

        Film film = Film.builder()
                .id(1L)
                .name("About Desert")
                .description("A film about desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(130)
                .likes(0L)
                .genres(genres)
                .mpa(RatingMpa.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .build();
        return filmStorage.create(film);
    }

    private User createUser() {
        User user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        return userStorage.create(user);
    }

    @BeforeAll
    public static void beforeAll() {
        modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate, modelMapper);
        RatingMpaStorage ratingMpaStorage = new RatingMpaDbStorage(jdbcTemplate, modelMapper);
        userStorage = new UserDbStorage(jdbcTemplate, modelMapper);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage,
                ratingMpaStorage, userStorage);
    }

    @Test
    public void testCreateFindFilmByIdAndContains() {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder()
                .id(5)
                .name("Документальный")
                .build());

        Film film = Film.builder()
                .id(1L)
                .name("About Desert")
                .description("A film about desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(130)
                .likes(0L)
                .genres(genres)
                .mpa(RatingMpa.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .build();

        // Проверка метода create()
        Film newFilm = filmStorage.create(film);
        Film filmWithCorrectId = film.withId(newFilm.getId());

        assertThat(newFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithCorrectId);

        // Проверка метода getFilmById()
        Film savedFilm = filmStorage.getFilmById(filmWithCorrectId.getId());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmWithCorrectId);

        // Проверка метода contains()
        boolean isFilmExist = filmStorage.contains(filmWithCorrectId.getId());
        assertTrue(isFilmExist, "Film с id " + filmWithCorrectId.getId() + " не найден.");
    }

    @Test
    public void testUpdateAndDeleteFilm() {
        Film film = createFilm();

        boolean isFilmExist = filmStorage.contains(film.getId());
        assertTrue(isFilmExist, "Film с id " + film.getId() + " не найден.");

        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build());
        genres.add(Genre.builder()
                .id(5)
                .name("Документальный")
                .build());

        Film toUpdateFilm = Film.builder()
                .id(film.getId())
                .name("A Desert")
                .description("Great Sahara Desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(150)
                .likes(0L)
                .genres(genres)
                .mpa(RatingMpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();

        // Проверка метода updateFilm()
        Film updatedFilm = filmStorage.updateFilm(toUpdateFilm);

        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(toUpdateFilm);

        // Проверка метода deleteFilm()
        Film deletedFilm = filmStorage.deleteFilm(updatedFilm.getId());

        assertThat(deletedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);

        boolean isDelFilmExist = filmStorage.contains(updatedFilm.getId());
        assertFalse(isDelFilmExist, "Film с id " + film.getId() + " найден.");
    }

    @Test
    public void testFindAllFilms() {
        Film film1 = createFilm();

        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build());
        Film secondFilm = Film.builder()
                .id(1L)
                .name("A Desert")
                .description("Great Sahara Desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(150)
                .likes(0L)
                .genres(genres)
                .mpa(RatingMpa.builder()
                        .id(1)
                        .name("G")
                        .build())
                .build();
        Film film2 = filmStorage.create(secondFilm);

        Film thirdFilm = Film.builder()
                .id(1L)
                .name("Some Film")
                .description("Good film")
                .releaseDate(LocalDate.of(2019, 1, 1))
                .duration(90)
                .likes(0L)
                .mpa(RatingMpa.builder()
                        .id(2)
                        .name("PG")
                        .build())
                .build();
        Film film3 = filmStorage.create(thirdFilm);

        List<Film> films = List.of(film1, film2, film3);

        // Проверка метода findAllFilms()
        List<Film> savedFilms = filmStorage.findAllFilms();

        assertTrue(savedFilms.containsAll(films));
    }

    @Test
    public void testAddAndRemoveLike() {
        Film film = createFilm();
        User user = createUser();

        // Проверка метода addLike()
        filmStorage.addLike(film.getId(), user.getId());
        Film filmWithLike = filmStorage.getFilmById(film.getId());

        assertEquals(1L, filmWithLike.getLikes(), "У фильма с id "
                + filmWithLike.getId() + "нет лайка.");

        // Проверка метода deleteLike()
        filmStorage.deleteLike(film.getId(), user.getId());
        Film filmWithoutLike = filmStorage.getFilmById(film.getId());

        assertEquals(0L, filmWithoutLike.getLikes(), "У фильма с id "
                + filmWithoutLike.getId() + " есть лайк.");
    }

    @Test
    public void testGetTopFilms() {
        Film film1 = createFilm();
        Film film2 = createFilm();
        Film film3 = createFilm();

        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();

        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user2.getId());
        filmStorage.addLike(film2.getId(), user3.getId());

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user3.getId());

        filmStorage.addLike(film3.getId(), user2.getId());

        Film film1WithLike = filmStorage.getFilmById(film1.getId());
        Film film2WithLike = filmStorage.getFilmById(film2.getId());
        Film film3WithLike = filmStorage.getFilmById(film3.getId());
        List<Film> shouldTopFilmsList = new ArrayList<>();
        shouldTopFilmsList.add(film2WithLike);
        shouldTopFilmsList.add(film1WithLike);
        shouldTopFilmsList.add(film3WithLike);

        // Проверка метода getTopFilms()
        List<Film> topFilms = filmStorage.getTopFilms(3);

        assertThat(topFilms)
                .isEqualTo(shouldTopFilmsList);
    }
}
