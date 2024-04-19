package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.ratingMpa.RatingMpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingMpaController {
    private final RatingMpaService ratingMpaService;

    @GetMapping
    public List<RatingMpa> getAllRatings() {
        return ratingMpaService.getAllRatings();
    }

    @GetMapping("/{id}")
    public RatingMpa getRatingById(@PathVariable int id) {
        return ratingMpaService.getRatingById(id);
    }
}
