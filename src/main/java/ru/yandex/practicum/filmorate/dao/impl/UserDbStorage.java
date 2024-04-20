package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public User create(User user) {
        log.debug("Запрос создать нового пользователя.");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        return user.withId(userId);
    }

    @Override
    public User update(User user) {
        log.debug("Запрос обновить пользователя с id {}.", user.getId());
        String sql = "UPDATE users SET email = ?, login = ?,"
                + " name = ?, birthday = ? WHERE id = ?";

        int row = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (row != 1) {
            log.error("Запрос обновить несуществующего пользователя с id {}.", user.getId());
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", user.getId()));
        } else return user;
    }

    @Override
    public User delete(Long id) {
        log.debug("Получен запрос удалить пользователя с id {}.", id);

        User user = getUserById(id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);

        return user;
    }

    @Override
    public User getUserById(Long id) {
        log.debug("Запрос получить пользователя с id {}.", id);

        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, mapper::makeUser, id);
        if (users.isEmpty()) {
            log.error("Запрос получить несуществующего пользователя с id {}.", id);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", id));
        } else return users.get(0);
    }

    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, mapper::makeUser);
    }

    @Override
    public boolean contains(Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count == 1;
    }
}
