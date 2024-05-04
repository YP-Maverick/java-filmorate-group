package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    public void add(String type, String operation, Long userId, Long entityId);

    public List<Event> getAll(Long id);
}
