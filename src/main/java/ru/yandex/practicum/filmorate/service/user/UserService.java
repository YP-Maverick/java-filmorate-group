package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    UserStorage userStorage;
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    private boolean isFriend(Long userId, Long friendId) {
        Set<Long> userFriends = friends.get(userId);
        return userFriends.contains(friendId);
    }

    public void addFriend(Long userId, Long friendId) throws ValidationException {
        if ((!userStorage.contains(userId)) || (!userStorage.contains(friendId))) {
            log.error("Указан id несуществующего пользователя. Запрос: addFriend.");
            throw new ValidationException("Пользователя с таким id не существует.");
        }

        Set<Long> userFriends;

        if (friends.containsKey(userId)) {
            userFriends = friends.get(userId);
            userFriends.add(friendId);
        } else {
            userFriends = new HashSet<>(Collections.singleton(friendId));
            // userFriends.add(friendId); TODO: возможно, удалить!
        }
        friends.put(userId,userFriends);

        Set<Long> friendFriends;
        if (friends.containsKey(friendId)) {
            friendFriends = friends.get(friendId);
            friendFriends.add(userId);
        } else {
            friendFriends = new HashSet<>(Collections.singleton(userId));
            //friendFriends.add(userId); TODO: возможно, удалить!
        }
        friends.put(friendId, friendFriends);
    }

    public User deleteFriend() {
        return null;
    }

    public List<User> getAllFriends() {
        return null;
    }

    public List<User> getCommonFriends() {
        return null;
    }
}
