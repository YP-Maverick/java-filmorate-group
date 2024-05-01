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

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    public Long add(String type, String operation, Long userId, Long filmId) {
        Event event = Event.builder()
                .timeStamp(LocalDateTime.now())
                .type(type)
                .operation(operation)
                .user_id(userId)
                .entity_id(filmId)
                .build();
        log.debug("Запрос создать нового пользователя.");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");
        return simpleJdbcInsert.executeAndReturnKey(event.toMap()).longValue();
    }

    public List<Event> getAll(Long id) {
        String sqlQuery = "SELECT * FROM events WHERE user_id = ? ORDER BY event_timestamp;";
        return jdbcTemplate.query(sqlQuery, mapper::makeEvent, id);
    }
}
