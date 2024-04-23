package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.RatingMpaDbStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingMpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private static ModelMapper mapper;
    RatingMpaStorage ratingStorage;

    @BeforeAll
    public static void beforeAll() {
        mapper = new ModelMapper();
    }

    @BeforeEach
    public void beforeEach() {
        ratingStorage = new RatingMpaDbStorage(jdbcTemplate, mapper);
    }

    @Test
    public void testGetRatingById() {
        // Проверка метода checkRatingId()
        ratingStorage.checkRatingId(1);

        RatingMpa rating = RatingMpa.builder()
                .id(1)
                .name("G")
                .build();

        // Проверка метода getRatingById()
        RatingMpa savedRating = ratingStorage.getRatingById(1);

        assertThat(savedRating)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(rating);
    }

    @Test
    public void testGetAllRatings() {
        List<RatingMpa> ratings = new ArrayList<>();

        ratings.add(RatingMpa.builder()
                .id(1)
                .name("G")
                .build());
        ratings.add(RatingMpa.builder()
                .id(2)
                .name("PG")
                .build());
        ratings.add(RatingMpa.builder()
                .id(3)
                .name("PG-13")
                .build());
        ratings.add(RatingMpa.builder()
                .id(4)
                .name("R")
                .build());
        ratings.add(RatingMpa.builder()
                .id(5)
                .name("NC-17")
                .build());

        //Проверка метода getAllRatings()
        List<RatingMpa> savedRatings = ratingStorage.getAllRatings();

        assertThat(savedRatings)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(ratings);
    }
}
