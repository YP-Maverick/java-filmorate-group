package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private final int id;
    @NotBlank
    private final String name;
    @Size(max=200)
    private final String description;
    @NotNull
    @IsAfter
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
}
