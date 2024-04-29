package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {
    Director create(Director director);
    Director update(Director director);
    Director delete(Long id);
    List<Director> findAll();
    Director getById(Long id);
    Set<Director> getFilmDirectors(Long filmId);
    void addFilmDirectors(Long filmId, Set<Director> directors);
    void updateFilmDirectors(Long filmId, Set<Director> directors);

    void checkDirectors(Set<Director> directors);
}
