package ru.yandex.practicum.filmorate.model;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class RatingMPA {
    int id;
    @NotBlank(message = "Название рейтинга не может быть пустым.")
    String name;
}
