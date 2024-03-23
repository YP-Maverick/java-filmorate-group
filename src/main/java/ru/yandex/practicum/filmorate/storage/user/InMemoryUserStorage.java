package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 0L;
    private Long createId() {
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
    public User update(User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            log.error("Запрос обновить несуществующего пользователя.");
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        log.debug("Получен запрос обновить пользователя.");

        return users.put(user.getId(), user);
    }

    @Override
    public User delete(Long id) throws NotFoundException {
        if (!users.containsKey(id)) {
            log.error("Запрос удалить несуществующего пользователя.");
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        log.debug("Получен запрос удалить пользователя.");

        return users.remove(id);
    }

    @Override
    public User getUserById(Long id) throws NotFoundException {
       if (!users.containsKey(id)) {
           log.error("Запрос получить несуществующего пользователя.");
           throw new NotFoundException("Пользователя с таким id не существует.");
       }
        log.debug("Получен запрос получить пользователя по id.");

        return users.get(id);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean contains(Long id) {
        return users.containsKey(id);
    }
}
