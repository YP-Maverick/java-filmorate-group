package ru.yandex.practicum.filmorate.service.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

@AllArgsConstructor
@Service
@Slf4j
public class ReviewService {

    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;

    private void checkReviewId(Long reviewId) {

        if (!reviewStorage.contains(reviewId)) {
            log.error("Неверно указан id отзыва: {}.", reviewId);
            throw new NotFoundException(String.format("Отзыва с id %d не существует.",  reviewId));
        }
    }

    private void checkFilmId(Long filmId) {

        if (!filmStorage.contains(filmId)) {
            log.error("Неверно указан id фильма: {}.", filmId);
            throw new NotFoundException(String.format("Фильма с id %d не существует.",  filmId));
        }
    }

    private void checkUserId(Long userId) {

        if (!userStorage.contains(userId)) {
            log.error("Неверно указан id пользователя: {}.", userId);
            throw new NotFoundException(String.format("Пользователя с id %d не существует.",  userId));
        }
    }

    // Создание отзыва
    public Review createReview(Review review) {

        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());

        return reviewStorage.createReview(review);
    }

    // Обновление отзыва
    public Review updateReview(Review review) {

        checkReviewId(review.getId());

        return reviewStorage.updateReview(review);
    }

    // Получение отзыва по его идентификатору
    public Review getReview(Long reviewId) {

        checkReviewId(reviewId);

        return reviewStorage.getReview(reviewId);
    }

    // Добавление лайка к отзыву
    public void addLike(Long reviewId, Long userId) {

        checkUserId(userId);
        checkReviewId(reviewId);

        reviewStorage.addLike(reviewId, userId);
    }

    // Удаление лайка от отзыва
    public void deleteLike(Long reviewId, Long userId) {

        checkUserId(userId);
        checkReviewId(reviewId);

        reviewStorage.deleteLike(reviewId, userId);
    }

    // Добавление дизлайка к отзыву
    public void addDislike(Long reviewId, Long userId) {

        checkUserId(userId);
        checkReviewId(reviewId);

        reviewStorage.addDislike(reviewId, userId);
    }

    // Удаление дизлайка от отзыва
    public void deleteDislike(Long reviewId, Long userId) {

        checkUserId(userId);
        checkReviewId(reviewId);

        reviewStorage.addDislike(reviewId, userId);
    }
}
