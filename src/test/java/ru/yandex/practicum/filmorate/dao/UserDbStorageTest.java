package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private static ModelMapper modelMapper;
    private UserStorage userStorage;

    private User createUser() {
        User user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        return userStorage.create(user);
    }

    @BeforeAll
    public static void beforeAll() {
        modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate, modelMapper);
    }

    @Test
    public void testCreateFindUserByIdAndContains() {
        User user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User newUser = userStorage.create(user);

        User savedUser = userStorage.getUserById(newUser.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user.withId(newUser.getId()));

        boolean isUserExist = userStorage.contains(savedUser.getId());
        assertTrue(isUserExist, "User с id " + savedUser.getId() + "не найден.");
    }

    @Test
    public void testUpdateAndDeleteUser() {
        User newUser = createUser();

        boolean isUserExist = userStorage.contains(newUser.getId());
        assertTrue(isUserExist, "User с id " + newUser.getId() + "не найден.");

        User toUpdateUser = User.builder()
                .id(newUser.getId())
                .email("updateUser@email.ru")
                .login("vanya456")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User updatedUser = userStorage.update(toUpdateUser);

        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(toUpdateUser);

        User deletedUser = userStorage.delete(updatedUser.getId());

        assertThat(deletedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);

        boolean isDelUserExist = userStorage.contains(newUser.getId());
        assertFalse(isDelUserExist, "User с id " + newUser.getId() + " найден.");
    }

    @Test
    public void testFindAllUsers() {
        User user1 = createUser();

        User secondUser = User.builder()
                .id(2L)
                .email("secondUser@email.ru")
                .login("vanya456")
                .name("Foo Bar")
                .birthday(LocalDate.of(1990, 2, 2))
                .build();
        User user2 = userStorage.create(secondUser);

        User thirdUser = User.builder()
                .id(3L)
                .email("thirdUser@email.ru")
                .login("vanya789")
                .name("Vasya Pupkin")
                .birthday(LocalDate.of(1990, 3, 3))
                .build();
        User user3 = userStorage.create(thirdUser);

        List<User> users = List.of(user1, user2, user3);
        List<User> savedUsers = userStorage.findAllUsers();

        assertTrue(savedUsers.containsAll(users));
    }
}
