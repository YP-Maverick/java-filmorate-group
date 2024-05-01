package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.sql.In;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
    Long id;
    String type;
    String operation;
    LocalDateTime timeStamp;
    Long user_id;
    Long entity_id;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("type", type);
        values.put("operation", operation);
        values.put("event_timestamp", timeStamp);
        values.put("user_id", user_id);
        values.put("entity_id", entity_id);
        return values;
    }
}
