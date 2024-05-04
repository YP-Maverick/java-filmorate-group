package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    public void add(String eventType, String operation, Long userId, Long entityId) {
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(eventType)
                .operation(operation)
                .userId(userId)
                .entityId(entityId)
                .build();
        log.debug("Создано новое событие в историю");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");
        simpleJdbcInsert.executeAndReturnKey(event.toMap()).longValue();
    }

    public List<Event> getAll(Long id) {
        String sqlQuery = "SELECT EVENT_ID, \"TYPE\", OPERATION, EVENT_TIMESTAMP, USER_ID, ENTITY_ID FROM PUBLIC.EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, mapper::makeEvent, id);
    }
}
