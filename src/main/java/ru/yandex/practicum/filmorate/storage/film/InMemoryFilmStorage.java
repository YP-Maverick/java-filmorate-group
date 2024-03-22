package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    private Integer createId() {
        return ++id;
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Получен запрос создать новый фильм.");

        Film newFilm = Film.builder().id(createId()).name(film.getName()).description(film.getDescription())
                .releaseDate(film.getReleaseDate()).duration(film.getDuration()).build();

        return films.put(newFilm.getId(), newFilm);
    }

    @Override
    public Film deleteFilm(Integer id) {
        if (!films.containsKey(id)) {
            log.error("Запрос удалить несуществующий фильм.");
            throw new ValidationException("Фильма с таким id не существует.");
        }
        log.debug("Получен запрос удалить фильм.");

        return films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Запрос обновить несуществующий фильм.");
            throw new ValidationException("Фильма с таким id не существует.");
        }
        log.debug("Получен запрос обновить фильм.");

        return films.put(film.getId(), film);
    }

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }
}
