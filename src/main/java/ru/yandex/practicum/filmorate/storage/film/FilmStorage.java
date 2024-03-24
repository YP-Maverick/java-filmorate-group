package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);
    Film deleteFilm(Long id);
    Film updateFilm(Film film);
    Film getFilmById(Long id);
    List<Film> findAllFilms();
    boolean contains(Long id);
}
