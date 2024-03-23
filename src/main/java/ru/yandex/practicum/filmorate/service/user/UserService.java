package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {

    UserStorage userStorage;
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void checkId(Long id) {
        if (!userStorage.contains(id)) {
            log.error("Указан id несуществующего пользователя.");
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        checkId(userId);
        checkId(friendId);

        Set<Long> userFriends = friends.get(userId);
        userFriends.add(friendId);
        friends.put(userId,userFriends);

        Set<Long> friendFriends = friends.get(friendId);
        friendFriends.add(userId);
        friends.put(friendId, friendFriends);
    }

    public User deleteFriend(Long userId, Long friendId) throws NotFoundException {
        checkId(userId);
        checkId(friendId);

        if (!friends.get(userId).remove(friendId)) {
            log.error("Запрос удаления друга, которого нет в списке друзей.");
            throw new NotFoundException("Этого пользователя нет в списке друзей.");
        } else {
            Set<Long> friendFriends = friends.get(friendId);
            friendFriends.remove(userId);
            return userStorage.getUserById(friendId);
        }
    }

    public List<User> getAllFriends(Long id) throws NotFoundException {
        checkId(id);

        List<User> userFriends = new ArrayList<>();
        for (Long friendId : friends.get(id)) {
            User friend = userStorage.getUserById(friendId);
            userFriends.add(friend);
        }
        return userFriends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        checkId(userId);
        checkId(otherId);

        Set<Long> userFriends = friends.get(userId);
        userFriends.retainAll(friends.get(otherId));
        List<User> commonFriends = new ArrayList<>();
        for (Long id : userFriends) {
            commonFriends.add(userStorage.getUserById(id));
        }
        return commonFriends;
    }

    public User createUser(User user) {
        User newUser = userStorage.create(user);
        friends.put(newUser.getId(), new HashSet<>());
        return newUser;
    }

    public User updateUser(User user) throws NotFoundException {
        return userStorage.update(user);
    }

    public User deleteUser(Long id) throws NotFoundException {
        User delUser = userStorage.delete(id);
        for (Long friendId : friends.get(id)) {
            friends.get(friendId).remove(id);
        }
        return delUser;
    }

    public User getUserById(Long id) throws NotFoundException {
        return userStorage.getUserById(id);
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }
}
