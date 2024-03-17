package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);
    void deleteFilm(int id);
    Film updateFilm(Film film);
    List<Film> findAllFilms();
}
