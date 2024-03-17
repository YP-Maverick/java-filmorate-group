package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    private Integer createId() {
        return ++id;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен запрос создать новый фильм.");

        Film newFilm = Film.builder().id(createId()).name(film.getName()).description(film.getDescription())
                .releaseDate(film.getReleaseDate()).duration(film.getDuration()).build();

        films.put(newFilm.getId(), newFilm);
        return films.get(newFilm.getId());
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Запрос обновить несуществующий фильм.");
            throw new ValidationException("Фильма с таким id не существует.");
        }
        log.debug("Получен запрос обновить фильм.");

        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}
