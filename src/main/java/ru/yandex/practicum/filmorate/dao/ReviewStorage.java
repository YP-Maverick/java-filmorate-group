package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    boolean contains(Long reviewId);

    Review createReview(Review review);

    Review updateReview(Review review);

    Review deleteReview(Long reviewId);

    Review getReview(Long reviewId);

    List<Review> getAllReviews();

    List<Review> getReviewsByFilmId(Long filmId, int count);

    void addLike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
