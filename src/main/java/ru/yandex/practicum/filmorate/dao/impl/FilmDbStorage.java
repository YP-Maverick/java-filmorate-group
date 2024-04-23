package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.RatingMpaStorage;
import ru.yandex.practicum.filmorate.exception.LikeException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private final RatingMpaStorage ratingMpaStorage;
    private final UserStorage userStorage;

    // Не в ModelMapper.java, чтобы избежать зацикливания с GenreStorage и RatingMpaStorage
    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_Date").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(rs.getLong("likes"))
                .genres(new HashSet<>(genreStorage.getFilmGenres(rs.getLong("id"))))
                .mpa(ratingMpaStorage.getRatingById(rs.getInt("rating_id")))
                .build();
    }

    @Override
    public Film create(Film film) {
        log.debug("Запрос создать новый фильм.");

        // Проверка существования жанров и рейтинга MPA
        for (Genre genre : film.getGenres()) {
            genreStorage.checkGenreId(genre.getId());
        }
        ratingMpaStorage.checkRatingId(film.getMpa().getId());

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

        // Проверка существования жанров и рейтинга MPA
        for (Genre genre : film.getGenres()) {
            genreStorage.checkGenreId(genre.getId());
        }
        ratingMpaStorage.checkRatingId(film.getMpa().getId());

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

        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, id);
        if (films.isEmpty()) {
            log.error("Запрос получить фильм по неверному id {}.", id);
            throw new NotFoundException(String.format("Фильма с id %d не существует.", id));
        } else {
            Film film = films.get(0);
            List<Genre> genres = genreStorage.getFilmGenres(film.getId());
            return film.withGenres(new HashSet<>(genres));
        }
    }

    @Override
    public List<Film> findAllFilms() {
        log.debug("Запрос получить список всех фильмов.");

        String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, this::makeFilm);
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

        // Проверка наличия фильма и пользователя
        getFilmById(filmId);
        userStorage.getUserById(userId);

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

        // Проверка наличия фильма и пользователя
        getFilmById(filmId);
        userStorage.getUserById(userId);

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
        String sql = "SELECT * FROM films  ORDER BY likes DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm, size);
    }
}
