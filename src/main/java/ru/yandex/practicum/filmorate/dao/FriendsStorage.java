package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(Long userId, Long friendId);
    long deleteFriend(Long userId, Long friendId);
    List<User> getAllFriends(Long id);
    List<User> getCommonFriends(Long userId, Long otherId);

}
