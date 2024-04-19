package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class Genre {
    int id;
    @NotBlank(message = "Название жанра не может быть пустым.")
    String name;
}
