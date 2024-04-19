package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@Builder
public class RatingMpa {
    int id;
    @NotBlank(message = "Название рейтинга не может быть пустым.")
    String name;
}
