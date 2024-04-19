package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(rs.getInt("likes"))
                .genres_id(genreStorage.getFilmGenres(rs.getLong("id")))
                .ratingMPA_id(rs.getInt("rating_id"))
                .build();
    }
    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film deleteFilm(Long id) {
        if (!contains(id)) {
            log.error("Запрос удалить несуществующий фильм с id {}.", id);
            throw new NotFoundException(String.format("Фильма с id %d не существует.", id));
        }
        log.debug("Получен запрос удалить фильм с id {}.", id);

        Film film = getFilmById(id);

        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!contains(film.getId())) {
            log.error("Запрос обновить несуществующий фильм с id {}.", film.getId());
            throw new NotFoundException(String.format("Фильма с id %d не существует.", film.getId()));
        }
        log.debug("Получен запрос обновить фильм с id {}.", film.getId());

        String sql = "UPDATE films SET name = ?, description = ?,"
                + " releaseDate = ?, duration = ?, likes = ?,"
                + " rating_id = ? WHERE id = ?";

        //TODO: добавить жанры
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getLikes(),
                film.getRatingMPA_id());

        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return null;
    }

    @Override
    public List<Film> findAllFilms() {
        return null;
    }

    @Override
    public boolean contains(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count == 1;
    }
}
