package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.yandex.practicum.filmorate.validation.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
@Builder
public class Film {
    //@With
    Long id;
    @NotBlank(message = "Название фильма не должно быть пустым")
    String name;
    @Size(max = 200, message = "Размер описания должен быть не больше 200 символов")
    String description;
    @NotNull(message = "Дату релиза необходимо задать")
    @IsAfter
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше 0")
    int duration;
    @With
    @PositiveOrZero
    Integer likes;
}
