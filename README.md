# java-filmorate
Template repository for Filmorate project.

![Database diagram](/DB Filmorate.jpg)

## Примеры запросов:
Получить список всех фильмов:
```
SELECT * 
FROM film;
```
Получить список всехопльзователей:
```
SELECT * 
FROM user;
```
Получить топ фильмов:
```
SELECT *
FROM film
ORDER BY likes DESC;
```
Получить список всех друзей пользователя с id x:
```
SELECT *
FROM user AS u
WHERE u.user_id IN (SELECT f.friend_id
		    FROM friend AS f
		    WHERE f.user_id = x AND
		    f.status = TRUE);
```