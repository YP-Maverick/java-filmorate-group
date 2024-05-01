package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {

    private static ModelMapper modelMapper;
    private final JdbcTemplate jdbcTemplate;

    private ReviewStorage reviewStorage;
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeAll
    public static void beforeAll() {
        modelMapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {

        reviewStorage = new ReviewDbStorage(jdbcTemplate, modelMapper);
        filmStorage = new FilmDbStorage(jdbcTemplate, modelMapper);
        userStorage = new UserDbStorage(jdbcTemplate, modelMapper);

        prepareFilms();
        prepareUsers();
    }

    private void prepareFilms() {

        Film film1 = Film.builder()
                .name("Inception")
                .description("A thief who enters the dreams of others to steal their secrets")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(RatingMpa.builder().id(2).build())
                .build();

        Film film2 = Film.builder()
                .name("The Shawshank Redemption")
                .description("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.")
                .releaseDate(LocalDate.of(1994, 10, 14))
                .duration(142)
                .mpa(RatingMpa.builder().id(2).build())
                .build();

        Film film3 = Film.builder()
                .name("Finding Nemo")
                .description("After his son is captured in the Great Barrier Reef and taken to Sydney, a timid clownfish sets out on a journey to bring him home.")
                .releaseDate(LocalDate.of(2003, 5, 30))
                .duration(100)
                .mpa(RatingMpa.builder().id(2).build())
                .build();

        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.create(film3);
    }

    private void prepareUsers() {

        User user1 = User.builder()
                .id(0L)
                .email("user1@example.com")
                .login("user1")
                .name("Alice")
                .birthday(LocalDate.of(1990, 5, 15))
                .build();

        User user2 = User.builder()
                .id(0L)
                .email("user2@example.com")
                .login("user2")
                .name("Bob")
                .birthday(LocalDate.of(1985, 8, 20))
                .build();

        User user3 = User.builder()
                .id(0L)
                .email("user3@example.com")
                .login("user3")
                .name("Charlie")
                .birthday(LocalDate.of(1995, 3, 10))
                .build();

        User user4 = User.builder()
                .id(0L)
                .email("user4@example.com")
                .login("user4")
                .name("David")
                .birthday(LocalDate.of(1988, 11, 25))
                .build();

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);
        userStorage.create(user4);
    }

    @Test
    public void testCreateReview() {

        Review review = Review.builder()
                .content("Great Movie!")
                .isPositive(true)
                .userId(1L)
                .filmId(3L)
                .build();

        Review createdReview = reviewStorage.createReview(review);
        assertNotNull(createdReview);
        assertNotNull(createdReview.getId());
        assertEquals(review.getContent(), createdReview.getContent());
    }

    @Test
    public void testGetReview() {

        Review review = Review.builder()
                .content("Great Movie!")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Review createdReview = reviewStorage.createReview(review);
        Review foundReview = reviewStorage.getReview(createdReview.getId());
        assertNotNull(foundReview);
        assertEquals("Great Movie!", foundReview.getContent());
    }

    @Test
    public void testUpdateReview() {

        Review review = Review.builder()
                .content("content")
                .isPositive(true)
                .userId(2L)
                .filmId(1L)
                .build();

        Review createdReview = reviewStorage.createReview(review);

        Review newReview = Review.builder()
                .id(createdReview.getId())
                .content("Updated content")
                .isPositive(false)
                .build();
        Review updatedReview = reviewStorage.updateReview(newReview);

        assertNotNull(updatedReview);
        assertEquals("Updated content", updatedReview.getContent());
    }

    @Test
    public void testDeleteReview() {
        Review review = Review.builder()
                .content("fff")
                .isPositive(true)
                .userId(3L)
                .filmId(3L)
                .build();

        Review createdReview = reviewStorage.createReview(review);

        reviewStorage.deleteReview(createdReview.getId());
        assertEquals(Collections.EMPTY_LIST, reviewStorage.getAllReviews());
    }

    @Test
    public void testGetAllReviews() {
        Review review1 = Review.builder()
                .content("Great Movie!")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        Review review2 = Review.builder()
                .content("Bad Movie!")
                .isPositive(false)
                .userId(1L)
                .filmId(2L)
                .build();

        Review review3 = Review.builder()
                .content("Great Movie!")
                .isPositive(true)
                .userId(1L)
                .filmId(3L)
                .build();

        List<Review> reviews = List.of(review1, review2, review3);

        reviews.forEach(review -> {
            Review createdRev = reviewStorage.createReview(review);
            review.withId(createdRev.getId());
        });

        assertEquals(reviews.size(), reviewStorage.getAllReviews().size());
    }
}