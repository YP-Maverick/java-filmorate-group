package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenreById(int id);
    List<Genre> getAllGenres();
    List<Integer> getFilmGenres(Long filmId);
    void addFilmGenres(Long filmId, List<Integer> genresId);

    void updateFilmGenres(Long filmId, List<Integer> genresId);
}
