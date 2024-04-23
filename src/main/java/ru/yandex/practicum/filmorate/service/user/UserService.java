package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    private void checkId(Long userId) {
        if (!userStorage.contains(userId)) {
            log.error("Неверно указан id пользователя: {}.", userId);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.",  userId));
        }
    }

    public void addFriend(Long userId, Long friendId) {
        checkId(userId);
        checkId(friendId);

        friendsStorage.addFriend(userId, friendId);
    }

    public long deleteFriend(Long userId, Long friendId) {
        checkId(userId);
        checkId(friendId);

        return friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getAllFriends(Long id) {
        checkId(id);

        return friendsStorage.getAllFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return friendsStorage.getCommonFriends(userId, otherId);
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User deleteUser(Long id) {
        return userStorage.delete(id);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }
}
