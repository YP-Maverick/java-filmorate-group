package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer id = 0;
    private Integer createId() {
        return ++id;
    }

    @Override
    public User create(User user) {
        log.debug("Получен запрос создать нового пользователя.");

        User newUser = User.builder().id(createId()).email(user.getEmail()).login(user.getLogin())
                .name(user.getName()).birthday(user.getBirthday()).build();

        return users.put(newUser.getId(), newUser);
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Запрос обновить несуществющего пользователя.");
            throw new ValidationException("Пользователя с таким id не существует.");
        }
        log.debug("Получен запрос обновить пользователя.");

        return users.put(user.getId(), user);
    }

    @Override
    public User delete(Integer id) {
        if (!users.containsKey(id)) {
            log.error("Запрос удалить несуществющего пользователя.");
            throw new ValidationException("Пользователя с таким id не существует.");
        }
        log.debug("Получен запрос удалить пользователя.");

        return users.remove(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }
}
