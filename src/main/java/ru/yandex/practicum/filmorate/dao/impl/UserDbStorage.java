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
        log.debug("Получен запрос создать нового пользователя.");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        return user.withId(userId);
    }

    @Override
    public User update(User user) {
        if (!contains(user.getId())) {
            log.error("Запрос обновить несуществующего пользователя с id {}.", user.getId());
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", user.getId()));
        }
        log.debug("Получен запрос обновить пользователя с id {}.", user.getId());
        String sql = "UPDATE users SET email = ?, login = ?,"
                + " name = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User delete(Long id) {
        if (!contains(id)) {
            log.error("Запрос удалить несуществующего пользователя с id {}.", id);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", id));
        }
        log.debug("Получен запрос удалить пользователя с id {}.", id);

        User user = getUserById(id);
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);

        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (!contains(id)) {
            log.error("Запрос получить несуществующего пользователя с id {}.", id);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", id));
        }
        log.debug("Получен запрос получить пользователя с id {}.", id);

        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, mapper::makeUser, id);
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
