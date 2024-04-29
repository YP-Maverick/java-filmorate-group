package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
@Builder
public class Review {

    @With
    Long id; // Id отзыва

    @NotBlank(message = "Текст отзыва = NULL")
    String content;

    @NotNull(message = "Не указан тип отзыва, isPositive = NULL")
    boolean isPositive; // Положительный/Отрицательный отзыв

    @NotNull(message = "UserId не может быть равен NULL")
    @Positive
    Long userId; // Id автора отзыва

    @NotNull(message = "FilmId не может быть равен NULL")
    @Positive(message = "FilmId не может быть меньше 0")
    Long filmId; // Id оцениваемого фильма

    // Рейтинг полезности:
    // При лайке +1 ; При дизлайке -1
    @NotNull(message = "Useful = null")
    Integer useful;
}
