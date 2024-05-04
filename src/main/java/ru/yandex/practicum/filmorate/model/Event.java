package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
    @Positive
    Long eventId;
    String eventType;
    String operation;
    Long timestamp;
    Long userId;
    Long entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("event_id", eventId);
        values.put("type", eventType);
        values.put("operation", operation);
        values.put("event_timestamp", timestamp);
        values.put("user_id", userId);
        values.put("entity_id", entityId);
        return values;
    }
}
