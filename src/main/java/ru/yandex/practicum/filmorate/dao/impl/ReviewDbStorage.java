package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class ReviewDbStorage implements ReviewStorage {


    @Override
    public boolean contains(Long reviewId) {
        return false;
    }

    @Override
    public Review createReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public Review getReview(Long reviewId) {
        return null;
    }

    @Override
    public void addLike(Long reviewId, Long userId) {

    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {

    }

    @Override
    public void addDislike(Long reviewId, Long userId) {

    }
}
