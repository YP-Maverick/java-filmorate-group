package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenreById(int id);
    List<Genre> getAllGenres();
    List<Genre> getFilmGenres(Long filmId);
    List<Genre> addFilmGenres(Long filmId, List<Genre> genres);

    void updateFilmGenres(Long filmId, List<Genre> genres);

    void checkGenreId(Integer genreId);
}
