package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

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
        Film film = Film.builder()
                .id(1L)
                .name("About Desert")
                .description("A film about desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(130)
                .likes(0L)
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
        userStorage = new UserDbStorage(jdbcTemplate, modelMapper);
        filmStorage = new FilmDbStorage(jdbcTemplate, modelMapper);
    }

    @Test
    public void testCreateFindFilmByIdAndContains() {
        Film film = Film.builder()
                .id(1L)
                .name("About Desert")
                .description("A film about desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(130)
                .likes(0L)
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

        Film toUpdateFilm = Film.builder()
                .id(film.getId())
                .name("A Desert")
                .description("Great Sahara Desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(150)
                .likes(0L)
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

        Film secondFilm = Film.builder()
                .id(1L)
                .name("A Desert")
                .description("Great Sahara Desert")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(150)
                .likes(0L)
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
        List<Film> topFilms = filmStorage.getTopFilms(3, null,"2020");
        List<Film> incorrectTopFilms = filmStorage.getTopFilms(3, null, "1990");
        List<Film> emptyList = new ArrayList<>();

        assertThat(topFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(shouldTopFilmsList);

        assertThat(incorrectTopFilms)
                .isEqualTo(emptyList);
    }

    @Test
    public void testGetCommonFilms() {
        Film film1 = createFilm();
        Film film2 = createFilm();
        Film film3 = createFilm();
        Film film4 = createFilm();

        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        User user4 = createUser();

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film3.getId(), user1.getId());
        filmStorage.addLike(film4.getId(), user1.getId());

        filmStorage.addLike(film2.getId(), user2.getId());
        filmStorage.addLike(film3.getId(), user2.getId());
        filmStorage.addLike(film4.getId(), user2.getId());

        filmStorage.addLike(film4.getId(), user3.getId());

        Film film4WithLike = filmStorage.getFilmById(film4.getId());
        Film film3WithLike = filmStorage.getFilmById(film3.getId());
        List<Film> shouldCommonFilmsList = new ArrayList<>();
        shouldCommonFilmsList.add(film4WithLike);
        shouldCommonFilmsList.add(film3WithLike);

        // Проверка метода getCommonFilms()
        List<Film> commonFilms = filmStorage.getCommonFilms(user1.getId(), user2.getId());
        List<Film> incorrectTopFilms = filmStorage.getCommonFilms(user3.getId(), user4.getId());
        List<Film> emptyList = new ArrayList<>();

        assertThat(commonFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(shouldCommonFilmsList);

        assertThat(incorrectTopFilms)
                .isEqualTo(emptyList);
    }
}
