package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

public interface RatingMpaStorage {
    RatingMpa getRatingById(int id);

    List<RatingMpa> getAllRatings();

    void checkRatingId(Integer id);

    RatingMpa getFilmRating(Long filmId);
}
