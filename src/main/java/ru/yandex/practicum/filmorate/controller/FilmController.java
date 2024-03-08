package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/film")
    public Film create(@RequestBody Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @PutMapping(value = "/film")
    public Film updateOrCreate(@RequestBody Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }
}
