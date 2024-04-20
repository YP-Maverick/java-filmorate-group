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
    List<Integer> genresId;
    int ratingMPA_id;


    public Film(Long id, String name, String description, LocalDate releaseDate, int duration,
                Long likes, List<Integer> genresId, int ratingMPA_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        this.genresId = (genresId == null) ? new ArrayList<>() : genresId;
        this.ratingMPA_id = ratingMPA_id;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_Date", releaseDate);
        values.put("duration", duration);
        values.put("ratingMPA_id", ratingMPA_id);
        return values;
    }
}
