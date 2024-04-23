package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.LikeException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final ModelMapper mapper;

    @Override
    public Film create(Film film) {
        log.debug("Запрос создать новый фильм.");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        List<Genre> genres = new ArrayList<>(film.getGenres());
        if (!genres.isEmpty()) {
            genres = genreStorage.addFilmGenres(filmId, genres);
            return film.withId(filmId).withGenres(new HashSet<>(genres));
        }
        return film.withId(filmId);
    }

    @Override
    public Film deleteFilm(Long id) {
        log.debug("Получен запрос удалить фильм с id {}.", id);

        Film film = getFilmById(id);

        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Запрос обновить фильм с id {}.", film.getId());

        String sql = "UPDATE films SET name = ?, description = ?, "
                + "release_date = ?, duration = ?, "
                + "rating_id = ? WHERE id = ?";

        int row = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (row != 1) {
            log.error("Запрос обновить несуществующий фильм с id {}.", film.getId());
            throw new NotFoundException(String.format("Фильма с id %d не существует.", film.getId()));
        } else {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            genres = genreStorage.updateFilmGenres(film.getId(), genres);
            return film.withGenres(new HashSet<>(genres));
        }
    }

    @Override
    public Film getFilmById(Long id) {
        log.debug("Запрос получить фильм по id {}.", id);

        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "WHERE f.id = ?;";
        List<Film> films = jdbcTemplate.query(sql, mapper::makeFilm, id);
        if (films.isEmpty()) {
            log.error("Запрос получить фильм по неверному id {}.", id);
            throw new NotFoundException(String.format("Фильма с id %d не существует.", id));
        } else return films.get(0);
    }

    @Override
    public List<Film> findAllFilms() {
        log.debug("Запрос получить список всех фильмов.");

        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id ";
        return jdbcTemplate.query(sql, mapper::makeFilm);
    }

    @Override
    public boolean contains(Long id) {
        log.debug("Запрос проверить наличие в БД  фильма с id {}.", id);

        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count == 1;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.debug("Запрос пользователя с id {} добавить лайк фильму с id {}.", userId, filmId);

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DataAccessException e) {
            log.error("Запрос пользователя ({}) повторно поставить лайк фильму ({}).", userId, filmId);
            throw new LikeException(String.format("Пользователь %d уже поставил лайк фильму %d.",
                    userId, filmId));
        }
        String filmLikeSql = "UPDATE films SET likes = likes + 1 WHERE id = ?";
        jdbcTemplate.update(filmLikeSql, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        log.debug("Запрос пользователя с id {} удалить лайк у фильма с id {}.", userId, filmId);

        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ? ";
        int row = jdbcTemplate.update(sql, filmId, userId);
        if (row == 1) {
            String filmLikeSql = "UPDATE films SET likes = likes - 1 WHERE id = ?";
            jdbcTemplate.update(filmLikeSql, filmId);
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        log.debug("Получен запрос вывести список популярных фильмов");

        long size = (count == null || count <= 0) ? 10L : count;
        String sql = "SELECT f.*, "
                + "rm.name AS rating_name "
                + "FROM films f "
                + "JOIN rating_MPA rm ON rm.ID = f.rating_id "
                + "ORDER BY likes DESC LIMIT ?";
        return jdbcTemplate.query(sql, mapper::makeFilm, size);
    }
}
