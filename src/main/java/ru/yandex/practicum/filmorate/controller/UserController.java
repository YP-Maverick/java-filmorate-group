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
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    private Integer createId() {
        return ++id;
    }

    @PostMapping
    public User create(@Valid  @RequestBody User user) {
        log.debug("Получен запрос создать нового пользователя.");

        User newUser = User.builder().id(createId()).email(user.getEmail()).login(user.getLogin())
                .name(user.getName()).birthday(user.getBirthday()).build();

        users.put(newUser.getId(), newUser);
        return users.get(newUser.getId());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Запрос обновить несуществющего пользователя.");
            throw new ValidationException("Пользователя с таким id не существует.");
        }
        log.debug("Получен запрос обновить пользователя.");

        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
