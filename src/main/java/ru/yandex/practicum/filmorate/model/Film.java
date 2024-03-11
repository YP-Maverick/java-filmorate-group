package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Value
@Builder
public class Film {
    int id;
    @NotBlank
    String name;
    @Size(max=200)
    String description;
    @NotNull
    @IsAfter
    LocalDate releaseDate;
    @Positive
    int duration;
}
