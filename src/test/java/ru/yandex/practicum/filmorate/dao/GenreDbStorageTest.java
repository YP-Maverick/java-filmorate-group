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
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private static ModelMapper modelMapper;

    private GenreStorage genreStorage;

    @BeforeAll
    public static void beforeAll() {
        modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        genreStorage = new GenreDbStorage(jdbcTemplate, modelMapper);
        userStorage = new UserDbStorage(jdbcTemplate, modelMapper);
        RatingMpaStorage ratingMpaStorage = new RatingMpaDbStorage(jdbcTemplate, modelMapper);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage,
                ratingMpaStorage, userStorage);
    }

    @Test
    public void testGetByIdAndGetAll() {
        // Проверка метода checkGenreId()
        genreStorage.checkGenreId(1);

        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        // Проверка метода getGenreById()
        Genre savedGenre = genreStorage.getGenreById(1);

        assertThat(savedGenre)
                .isNotNull()
                .isEqualTo(genre);

        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        genres.add(Genre.builder()
                .id(2)
                .name("Драма")
                .build());
        genres.add(Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build());
        genres.add(Genre.builder()
                .id(4)
                .name("Триллер")
                .build());
        genres.add(Genre.builder()
                .id(5)
                .name("Документальный")
                .build());
        genres.add(Genre.builder()
                .id(6)
                .name("Боевик")
                .build());

        // Проверка метода getAllGenres()
        List<Genre> savedGenres = genreStorage.getAllGenres();

        assertThat(savedGenres)
                .isNotNull()
                .isEqualTo(genres);
    }

    @Test
    public void testAddGetAndUpdateFilmGenres() {
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
        Film newfilm = filmStorage.create(film);

        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        genres.add(Genre.builder()
                .id(4)
                .name("Триллер")
                .build());

        // Проверка метода addFilmGenres()
        List<Genre> savedGenres = genreStorage.addFilmGenres(newfilm.getId(), genres);
        assertThat(savedGenres)
                .isNotNull()
                .isEqualTo(genres);

        // Проверка метода getFilmGenres()
        List<Genre> filmGenres = genreStorage.getFilmGenres(newfilm.getId());
        assertThat(filmGenres)
                .isNotNull()
                .isEqualTo(genres);

        List<Genre> toUpdateGenres = new ArrayList<>();
        genres.add(Genre.builder()
                .id(2)
                .name("Драма")
                .build());
        genres.add(Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build());

        // Проверка метода updateFilmGenres()
        List<Genre> updatedGenres = genreStorage.updateFilmGenres(newfilm.getId(), toUpdateGenres);
        assertThat(updatedGenres)
                .isNotNull()
                .isEqualTo(toUpdateGenres);

        List<Genre> newFilmGenres = genreStorage.getFilmGenres(newfilm.getId());
        assertThat(newFilmGenres)
                .isNotNull()
                .isEqualTo(updatedGenres);
    }
 }
