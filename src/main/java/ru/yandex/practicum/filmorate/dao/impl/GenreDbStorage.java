package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
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
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genre = jdbcTemplate.query(sql, mapper::makeGenre, id);
        if (genre.isEmpty()) {
            log.error("Запрос получить жанр по неверному id {}.", id);
            throw new NotFoundException(String.format("Жанра с id %d не существует.", id));
        } else return genre.get(0);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres GROUP BY id";
        return jdbcTemplate.query(sql, mapper::makeGenre);
    }

    @Override
    public List<Integer> getFilmGenres(Long filmId) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("genre_id"), filmId);
    }
}
