package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.yandex.practicum.filmorate.validation.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Value
@Builder
public class Film {
    @With
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
    Long likes;
    @With
    List<Genre> genres;
    RatingMpa mpa;


    public Film(Long id, String name, String description, LocalDate releaseDate, int duration,
                Long likes, List<Genre> genres, RatingMpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.genres = (genres == null) ? new ArrayList<>() : genres;
        this.mpa = mpa;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_Date", releaseDate);
        values.put("duration", duration);
        values.put("likes", 0L);
        values.put("rating_id", mpa.getId());
        return values;
    }
}
