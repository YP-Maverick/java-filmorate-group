package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class Director {
    @With
    Long id;
    @NotBlank
    String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        return values;
    }
}
