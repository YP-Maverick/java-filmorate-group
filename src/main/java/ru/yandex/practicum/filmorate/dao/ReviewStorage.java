package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    boolean contains(Long reviewId);

    Review createReview(Review review);

    Review updateReview(Review review);

    Review getReview(Long reviewId);

    void addLike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);
}
