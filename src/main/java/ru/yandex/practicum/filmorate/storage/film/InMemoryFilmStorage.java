package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 0L;

    private Long createId() {
        return ++id;
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Получен запрос создать новый фильм.");

        Film newFilm = Film.builder().id(createId()).name(film.getName()).description(film.getDescription())
                .releaseDate(film.getReleaseDate()).duration(film.getDuration()).likes(0).build();
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
}
