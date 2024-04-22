package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.GenreException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
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

        String sql = "SELECT * FROM genres WHERE id IN "
                + "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
        return jdbcTemplate.query(sql, mapper::makeGenre, filmId);
    }

    @Override
    public List<Genre> addFilmGenres(Long filmId, List<Genre> genres) {
        log.debug("Запрос добавить жанры фильма с id {}", filmId);

        String sql = "INSERT INTO film_genres (film_id, genre_id)"
                + "VALUES (?, ?)";
        List<Genre> genresToBeRemoved = new ArrayList<>();
        for (Genre genre : genres) {
            try {
                jdbcTemplate.update(sql, filmId, genre.getId());
            } catch(DuplicateKeyException e ) {
                genresToBeRemoved.add(genre);
            }
        }
        genres.removeAll(genresToBeRemoved);
        return genres;
    }

    @Override
    public List<Genre> updateFilmGenres(Long filmId, List<Genre> genres) {
        log.debug("Запрос обновить список жанров фильма с id {}", filmId);

        // Удаление прежнего списка жанров
        String delSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(delSql, filmId);

        // Добавление нового списка жанров
        return addFilmGenres(filmId, genres);
    }

    @Override
    public void checkGenreId(Integer genreId) {
        log.debug("Запрос проверить id {} жанра.", genreId);

        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genre = jdbcTemplate.query(sql, mapper::makeGenre, genreId);
        if (genre.isEmpty()) {
            log.error("Жанра с id {} нет в БД.", genreId);
            throw new GenreException(String.format("Неверно указан id (%d) жанра.",  genreId));
        }
    }
}
