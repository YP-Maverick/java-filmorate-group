package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;
import lombok.With;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class Review {

    @With
    @JsonProperty("reviewId")
    Long id; // Id отзыва

    @NotBlank(message = "Текст отзыва = NULL")
    String content;

    @NotNull(message = "Не указан тип отзыва, isPositive = NULL")
    @SerializedName("isPositive")
    @JsonProperty("isPositive")
    Boolean isPositive; // Положительный / Отрицательный отзыв

    @NotNull(message = "UserId не может быть равен NULL")
    Long userId; // Id автора отзыва

    @NotNull(message = "FilmId не может быть равен NULL")
    Long filmId; // Id оцениваемого фильма

    // Рейтинг полезности:
    // При лайке +1 ; При дизлайке -1
    Integer useful;
}
