package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.RatingMpaStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RatingException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class RatingMpaDbStorage implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;

    @Override
    public RatingMpa getRatingById(int id) {
        log.debug("Запрос получить рейтинг MPA по id {}.", id);

        String sql = "SELECT * FROM rating_MPA WHERE id = ?";
        List<RatingMpa> rating = jdbcTemplate.query(sql, mapper::makeRatingMpa, id);
        if (rating.isEmpty()) {
            log.error("Запрос получить рейтинг MPA по неверному id {}.", id);
            throw new NotFoundException(String.format("Рейтинга MPA с id %d не существует.", id));
        } else return rating.get(0);
    }

    @Override
    public List<RatingMpa> getAllRatings() {
        log.debug("Запрос получить список рейтингов MPA.");

        String sql = "SELECT * FROM rating_MPA GROUP BY id";
        return jdbcTemplate.query(sql, mapper::makeRatingMpa);
    }

    @Override
    public void checkRatingId(Integer id) {
        log.debug("Запрос проверить id {} рейтинга MPA.", id);

        String sql = "SELECT * FROM rating_MPA WHERE id = ?";
        List<RatingMpa> rating = jdbcTemplate.query(sql, mapper::makeRatingMpa, id);
        if (rating.isEmpty()) {
            log.error("Рейтинга MPA с id {} нет в БД.", id);
            throw new RatingException(String.format("Неверно указан id (%d) рейтинга MPA.", id));
        }
    }
}
