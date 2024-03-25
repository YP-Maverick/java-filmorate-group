package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final TreeSet<Film> filmRating = new TreeSet<>(Comparator.comparing(Film::getLikes)
            .thenComparing(Film::getId).reversed());

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;

    }

    private void checkFilmId(Long filmId) {
        if (!filmStorage.contains(filmId)) {
            log.error("Указан id несуществующего фильма.");
            throw new NotFoundException("Фильма с таким id не существует.");
        }
    }

    private void checkUserId(Long userId) {
        if (!userStorage.contains(userId)) {
            log.error("Указан id несуществующего пользователя.");
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        log.debug("Получен запрос добавить лайк.");

        Set<Long> users = likes.get(filmId);
        if (users.add(userId)) {
            Film film = filmStorage.getFilmById(filmId);
            Film newFilm = film.withLikes(film.getLikes() + 1);
            filmStorage.updateFilm(newFilm);
            filmRating.remove(film);
            filmRating.add(newFilm);
            likes.put(filmId, users);
        } else {
            log.error("Запрос повторно поставить лайк.");
            throw new ValidationException("Вы можете поставить лайк этому фильму только 1 раз.");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        log.debug("Получен запрос удалить лайк.");

        Set<Long> users = likes.get(filmId);
        if (users.remove(userId)) {
            Film film = filmStorage.getFilmById(filmId);
            Film newFilm = film.withLikes(film.getLikes() - 1);
            filmStorage.updateFilm(newFilm);
            filmRating.remove(film);
            filmRating.add(newFilm);
            likes.put(filmId, users);
        } else {
            log.error("Запрос удалить непоставленный лайк.");
            throw new ValidationException("Нельзя удалить лайк, который не ставили.");
        }
    }

    public List<Film> getTopFilms(Integer count) {
        log.debug("Получен запрос вывести список популярных фильмов");
        
        long size = (count == null || count <= 0) ? 10L : count;
        return filmRating.stream().limit(size).collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        Film newFilm = filmStorage.createFilm(film);
        likes.put(newFilm.getId(), new HashSet<>());
        filmRating.add(newFilm);
        return newFilm;
    }

    public Film deleteFilm(Long id) {
        likes.remove(id);
        Film delFilm = filmStorage.deleteFilm(id);
        filmRating.remove(delFilm);
        return delFilm;
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }
}
