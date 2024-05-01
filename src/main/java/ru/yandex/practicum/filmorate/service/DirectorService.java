package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public Director delete(Long id) {
        return directorStorage.delete(id);
    }

    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    public Director getById(Long id) {
        return directorStorage.getById(id);
    }
}
