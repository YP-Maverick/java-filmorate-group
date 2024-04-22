package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FriendsDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FriendsStorage friendsStorage;
    private  UserStorage userStorage;
    private static ModelMapper mapper;

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
        mapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate, mapper);
        friendsStorage = new FriendsDbStorage(jdbcTemplate, userStorage, mapper);
    }

    @Test
    public void testAddGetAndDeleteFriend() {
        User user = createUser();
        User friend = createUser();

        // Проверка методов addFriend() и getAllFriends()
        friendsStorage.addFriend(user.getId(), friend.getId());

        List<User> friends = friendsStorage.getAllFriends(user.getId());

        assertThat(friends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(friend));

        // Проверка метода deleteFriend()
        friendsStorage.deleteFriend(user.getId(), friend.getId());

        List<User> withoutfriends = friendsStorage.getAllFriends(user.getId());

        assertThat(withoutfriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new ArrayList<>());
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        User user4 = createUser();

        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user1.getId(), user3.getId());
        friendsStorage.addFriend(user1.getId(), user4.getId());

        friendsStorage.addFriend(user3.getId(), user2.getId());
        friendsStorage.addFriend(user3.getId(), user4.getId());

        List<User> commonFriends = List.of(user2, user4);

        // Проверка метода getCommonFriends()
        List<User> shouldCommonFriends = friendsStorage.getCommonFriends(user1.getId(), user3.getId());

        assertThat(shouldCommonFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(commonFriends);
    }

}
