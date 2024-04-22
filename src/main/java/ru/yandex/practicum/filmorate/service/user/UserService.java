package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@AllArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public void addFriend(Long userId, Long friendId) {
        friendsStorage.addFriend(userId, friendId);
    }

    public long deleteFriend(Long userId, Long friendId) {
        return friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getAllFriends(Long id) {
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
