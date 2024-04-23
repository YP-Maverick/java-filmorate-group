package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.RatingMpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final RatingMpaStorage ratingMpaStorage;

    private void checkFilmAndUserId(Long filmId, Long userId) {
        if (!filmStorage.contains(filmId)) {
            log.error("Неверно указан id фильма: {}.", filmId);
            throw new NotFoundException(String.format("Фильма с id %d не существует.",  filmId));
        } else if (!userStorage.contains(userId)) {
            log.error("Неверно указан id пользователя: {}.", userId);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.",  userId));
        }
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmAndUserId(filmId, userId);

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmAndUserId(filmId, userId);

        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    public Film createFilm(Film film) {
        // Проверка существования жанров и рейтинга MPA
        genreStorage.checkGenres(film.getGenres());
        ratingMpaStorage.checkRatingId(film.getMpa().getId());

        return filmStorage.create(film);
    }

    public Film deleteFilm(Long id) {
        return filmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        // Проверка существования жанров и рейтинга MPA
        genreStorage.checkGenres(film.getGenres());
        ratingMpaStorage.checkRatingId(film.getMpa().getId());

        return filmStorage.updateFilm(film);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        Set<Genre> genres = genreStorage.getFilmGenres(film.getId());

        return film.withGenres(genres);
    }
}
