package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film deleteFilm(Long id);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    List<Film> findAllFilms();

    boolean contains(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getTopFilms(Integer count, Integer genreId, String year);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
