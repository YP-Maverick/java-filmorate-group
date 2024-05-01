package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;
import java.util.Map;

public interface EventStorage {
    public Long add(String type, String operation, Long userId, Long filmId);

    public List<Event> getAll(Long id);
}
