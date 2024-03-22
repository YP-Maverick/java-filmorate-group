package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
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

    private void checkId(Long userId, Long otherId) {
        if ((!userStorage.contains(userId)) || (!userStorage.contains(otherId))) {
            log.error("Указан id несуществующего пользователя.");
            throw new ValidationException("Пользователя с таким id не существует.");
        }
    }

    public void addFriend(Long userId, Long friendId) throws ValidationException {
        checkId(userId, friendId);

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

    public User deleteFriend(Long userId, Long friendId) throws ValidationException, NotFoundException {
        checkId(userId, friendId);
        if (friends.containsKey(userId)) {
            Set<Long> userFriends = friends.get(userId);
            if (!userFriends.remove(friendId)) {
                log.error("Запрос удаления из друзей пользователя по id, которого нет в списке друзей.");
                throw new NotFoundException("Этого пользователя нет в списке друзей.");
            } else {
                Set<Long> friendFriends = friends.get(friendId);
                friendFriends.remove(userId);
                return userStorage.getUserById(friendId);
            }
        } else {
            log.error("Запрос удаления из друзей от пользователя, у которого нет друзей.");
            throw new ValidationException("Этого пользователя нет в списке друзей.");
        }
    }

    public List<User> getAllFriends() {
        return null;
    }

    public List<User> getCommonFriends() {
        return null;
    }
}
