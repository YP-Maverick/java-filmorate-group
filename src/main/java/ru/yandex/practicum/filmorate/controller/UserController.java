package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @PostMapping
    public User create(@Valid  @RequestBody User user) {
        return null;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return null;
    }

    @GetMapping
    public List<User> findAll() {
        return null;
    }
}
