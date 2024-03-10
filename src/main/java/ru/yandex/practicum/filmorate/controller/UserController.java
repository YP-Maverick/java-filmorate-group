package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/user")
    public User create(@Valid  @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.error("Запрос создать пользователя с используемым id.");
            throw new ValidationException("Пользователь с таким id уже существует.");
        }

        log.debug("Получен запрос создать нового пользователя.");

        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @PutMapping(value = "/user")
    public User updateOrCreate(@Valid @RequestBody User user) {
        log.debug("Получен запрос создать или обновить пользователя.");

        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @GetMapping(value = "/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
