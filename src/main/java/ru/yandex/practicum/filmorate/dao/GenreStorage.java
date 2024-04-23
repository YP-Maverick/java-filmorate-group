package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    Set<Genre> getFilmGenres(Long filmId);

    Set<Genre> addFilmGenres(Long filmId, Set<Genre> genres);

    Set<Genre> updateFilmGenres(Long filmId, Set<Genre> genres);

    void checkGenres(Set<Genre> genres);
}