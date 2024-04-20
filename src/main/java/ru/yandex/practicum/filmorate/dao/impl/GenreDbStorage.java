package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.GenreException;
import ru.yandex.practicum.filmorate.exception.LikeException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public Genre getGenreById(int id) {
        log.debug("Запрос получить жанр по id {}", id);

        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genre = jdbcTemplate.query(sql, mapper::makeGenre, id);
        if (genre.isEmpty()) {
            log.error("Запрос получить жанр по неверному id {}.", id);
            throw new NotFoundException(String.format("Жанра с id %d не существует.", id));
        } else return genre.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Запрос получить список всех жанров");

        String sql = "SELECT * FROM genres GROUP BY id";
        return jdbcTemplate.query(sql, mapper::makeGenre);
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        log.debug("Запрос получить список жанров фильма с id {}", filmId);

        String sql1 = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        String sql = "SELECT * FROM genres WHERE id IN "
                + "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
        return jdbcTemplate.query(sql, mapper::makeGenre, filmId);
    }

    @Override
    public void addFilmGenres(Long filmId, List<Genre> genres) {
        log.debug("Запрос добавить жанры фильма с id {}", filmId);

        String sql = "INSERT INTO film_genres (film_id, genre_id)"
                + "VALUES (?, ?)";
        for (Genre genre : genres) {
            try {
                jdbcTemplate.update (sql, filmId, genre.getId());
            } catch (DataIntegrityViolationException e) {
                log.error("Запрос добавить фильм с id {} с несуществующим жанром с id {}.",
                        filmId, genre.getId());
                throw new GenreException(String.format("Жанра с id {} не существует.", genre.getId()));
            }
        }
    }

    @Override
    public void updateFilmGenres(Long filmId, List<Genre> genres) {
        log.debug("Запрос обновить список жанров фильма с id {}", filmId);

        // Удаление прежнего списка жанров
        String delSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(delSql, filmId);

        // Добавление нового списка жанров
        addFilmGenres(filmId, genres);
    }
}
