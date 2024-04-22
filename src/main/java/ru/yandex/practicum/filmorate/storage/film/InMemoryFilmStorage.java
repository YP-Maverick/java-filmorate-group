package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final UserStorage userStorage;
    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final TreeSet<Film> filmRating = new TreeSet<>(Comparator.comparing(Film::getLikes)
            .thenComparing(Film::getId).reversed());

    private Long createId() {
        return ++id;
    }

    @Override
    public Film create(Film film) {
        log.debug("Получен запрос создать новый фильм.");

        Film newFilm = film.withId(createId())
                .withLikes(0L);
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film deleteFilm(Long id) {
        if (!films.containsKey(id)) {
            log.error("Запрос удалить несуществующий фильм.");
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        log.debug("Получен запрос удалить фильм.");

        return films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Запрос обновить несуществующий фильм.");
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        log.debug("Получен запрос обновить фильм.");

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            log.error("Запрос получить несуществующий фильм.");
            throw new NotFoundException("Фильма с таким id не существует.");
        }
        log.debug("Получен запрос получить фильм.");

        return films.get(id);
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean contains(Long id) {
        return films.containsKey(id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        log.debug("Получен запрос добавить лайк.");

        Set<Long> users = likes.get(filmId);
        if (users.add(userId)) {
            Film film = getFilmById(filmId);
            Film newFilm = film.withLikes(film.getLikes() + 1);
            updateFilm(newFilm);
            filmRating.remove(film);
            filmRating.add(newFilm);
            likes.put(filmId, users);
        } else {
            log.error("Запрос повторно поставить лайк.");
            throw new ValidationException("Вы можете поставить лайк этому фильму только 1 раз.");
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        checkFilmId(filmId);
        checkUserId(userId);
        log.debug("Получен запрос удалить лайк.");

        Set<Long> users = likes.get(filmId);
        if (users.remove(userId)) {
            Film film = getFilmById(filmId);
            Film newFilm = film.withLikes(film.getLikes() - 1);
            updateFilm(newFilm);
            filmRating.remove(film);
            filmRating.add(newFilm);
            likes.put(filmId, users);
        } else {
            log.error("Запрос удалить непоставленный лайк.");
            throw new ValidationException("Нельзя удалить лайк, который не ставили.");
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        log.debug("Получен запрос вывести список популярных фильмов");

        long size = (count == null || count <= 0) ? 10L : count;
        return filmRating.stream().limit(size).collect(Collectors.toList());
    }

    private void checkFilmId(Long filmId) {
        if (!contains(filmId)) {
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
}
