package ru.yandex.practicum.filmorate.model;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class Genre {
    int id;
    @NotBlank(message = "Название жанра не может быть пустым.")
    String name;
}
