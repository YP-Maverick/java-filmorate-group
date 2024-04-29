package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.LikeException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("checkstyle:Regexp")
@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public boolean contains(Long reviewId) {

        log.debug("Запрос проверить наличие в БД отзыв с id {}.", reviewId);

        String sql = "SELECT COUNT(*) FROM reviews WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count == 1;
    }

    @Override
    public Review createReview(Review review) {

        log.debug("Запрос создать новый отзыв на фильм.");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id",  review.getId());
        parameters.put("content",  review.getContent());
        parameters.put("is_positive",  review.isPositive());
        parameters.put("user_id",  review.getUserId());
        parameters.put("film_id",  review.getFilmId());
        parameters.put("useful0", review.getUseful());

        Long userId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        return review.withId(userId);
    }

    @Override
    public Review updateReview(Review review) {

        log.debug("Запрос обновить отзыв с id {}.", review.getId());

        String sql = "UPDATE reviews " +
                     "SET content = ?," +
                     " is_positive = ?," +
                     " user_id = ?, " +
                     "film_id = ?, " +
                     "useful = ? " +
                     "WHERE id = ?";

        int row = jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getId());

        if (row != 1) {
            log.error("Запрос обновить несуществующего пользователя с id {}.", review.getId());
            throw new NotFoundException(String.format("Пользователя с id %d не существует.", review.getId()));
        } else return review;
    }

    @Override
    public Review getReview(Long reviewId) {
        log.debug("Запрос получить пользователя с id {}.", reviewId);

        String sql = "SELECT * FROM users WHERE id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, mapper::maleReview, reviewId);

        return reviews.stream()
                      .findFirst()
                      .orElse(null);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        log.debug("Запрос пользователя с id {} добавить лайк отзыву с id {}.",
                userId, reviewId);

        String sql = "INSERT INTO review_likes (review_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, reviewId, userId);
        } catch (DataAccessException e) {
            log.error("Запрос пользователя ({}) повторно поставить лайк отзыву ({}).", userId, reviewId);
            throw new LikeException(String.format("Пользователь %d уже поставил лайк отзыву %d.",
                    userId, reviewId));
        }
        String reviewLikeSql = "UPDATE reviews SET useful = useful + 1 WHERE id = ?";
        jdbcTemplate.update(reviewLikeSql, reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {

        log.debug("Запрос пользователя с id {} удалить лайк у отзыва с id {}.", userId, reviewId);

        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";

        int row = jdbcTemplate.update(sql, reviewId, userId);
        if (row == 1) {
            String reviewDislikeSql = "UPDATE reviews SET useful = useful - 1 WHERE id = ?";
            jdbcTemplate.update(reviewDislikeSql, reviewId);
        }
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        log.debug("Запрос пользователя с id {} добавить дизлайк отзыву с id {}.",
                userId, reviewId);

        String sql = "INSERT INTO review_dislikes (review_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, reviewId, userId);
        } catch (DataAccessException e) {
            log.error("Запрос пользователя ({}) повторно поставить дизлайк отзыву ({}).", userId, reviewId);
            throw new LikeException(String.format("Пользователь %d уже поставил дизлайк отзыву %d.",
                    userId, reviewId));
        }
        String reviewDislikeSql = "UPDATE reviews SET useful = useful - 1 WHERE id = ?";
        jdbcTemplate.update(reviewDislikeSql, reviewId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        log.debug("Запрос пользователя с id {} удалить дизлайк у отзыва с id {}.", userId, reviewId);

        String sql = "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?";

        int row = jdbcTemplate.update(sql, reviewId, userId);
        if (row == 1) {
            String reviewDislikeSql = "UPDATE reviews SET useful = useful + 1 WHERE id = ?";
            jdbcTemplate.update(reviewDislikeSql, reviewId);
        }
    }
}
