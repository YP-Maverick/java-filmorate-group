package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;

import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private static ModelMapper modelMapper;
    private DirectorStorage directorStorage;

    @BeforeAll
    public static void beforeAll() {
        modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        directorStorage = new DirectorDbStorage(jdbcTemplate, modelMapper);
        filmStorage = new FilmDbStorage(jdbcTemplate, modelMapper);
    }

    private Director createDirector() {
        Director director = Director.builder()
                .name("New Director")
                .build();
        return directorStorage.create(director);
    }

    @Test
    public void testCreateAndGetById() {
        Director director = Director.builder()
                .name("New Director")
                .build();

        // Проверка метода create()
        Director newDirector = directorStorage.create(director);
        Director directorWithCorrectId = director.withId(newDirector.getId());

        assertThat(newDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(directorWithCorrectId);

        // Проверка метода getById()
        Director savedDirector = directorStorage.getById(directorWithCorrectId.getId());

        assertThat(savedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(directorWithCorrectId);
    }

    @Test
    public void testUpdateAndDeleteDirector() {
        Director director = createDirector();

        Director toUpdateDirector = Director.builder()
                .id(director.getId())
                .name("Updated Director")
                .build();

        // Проверка метода update()
        Director updatedDirector = directorStorage.update(toUpdateDirector);

        assertThat(updatedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(toUpdateDirector);

        // Проверка метода delete()
        Director deletedDirector = directorStorage.delete(updatedDirector.getId());

        assertThat(deletedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedDirector);

        // Проверка, что фильма нет в общем списке фильмов
        List<Director> directorList = directorStorage.findAll();
        assertFalse(directorList.contains(deletedDirector));
    }

    @Test
    public void testFindAllDirectors() {
        Director director1 = createDirector();

        Director secondDirector = Director.builder()
                .name("Foo Bar")
                .build();
        Director director2 = directorStorage.create(secondDirector);

        Director thirdDirector = Director.builder()
                .name("Some Director")
                .build();
        Director director3 = directorStorage.create(thirdDirector);

        List<Director> directors = List.of(director1, director2, director3);

        // Проверка методов checkDirectors() и findAll()
        Set<Director> directorSet = Set.of(director1, director2, director3);
        directorStorage.checkDirectors(directorSet);

        List<Director> savedDirectors = directorStorage.findAll();
        assertTrue(savedDirectors.containsAll(directors));
    }

    @Test
    public void testAddGetAndUpdateFilmDirectors() {
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

        Director director = createDirector();
        Set<Director> directors = Set.of(director);

        // Проверка методов addFilmDirectors() и getFilmDirectors()
        directorStorage.addFilmDirectors(newfilm.getId(), directors);

        Set<Director> savedDirectors = directorStorage.getFilmDirectors(newfilm.getId());
        assertThat(savedDirectors)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(directors);

        Director director2 = createDirector();
        Director director3 = createDirector();

        // Проверка метода updateFilmDirectors()
        Set<Director> toUpdateDirectors = Set.of(director2, director3);
        directorStorage.updateFilmDirectors(newfilm.getId(), toUpdateDirectors);

        Set<Director> updatedDirectors = directorStorage.getFilmDirectors(newfilm.getId());
        assertThat(updatedDirectors)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(toUpdateDirectors);
    }
}
