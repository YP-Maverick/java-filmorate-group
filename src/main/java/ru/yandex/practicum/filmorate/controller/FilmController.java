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
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/film")
    public Film create(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Запрос создать фильм с используемым id.");
            throw new ValidationException("Фильм с таким id уже существует.");
        }

        log.debug("Получен запрос создать новый фильм.");

        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @PutMapping(value = "/film")
    public Film updateOrCreate(@Valid @RequestBody Film film) {
        log.debug("Получен запрос обновить или создать фильм.");

        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}
