package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/user")
    public User create(@RequestBody User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @PutMapping(value = "/user")
    public User updateOrCreate(@RequestBody User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @GetMapping(value = "/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
