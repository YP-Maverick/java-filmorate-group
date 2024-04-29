package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.mapper.ModelMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Repository
@Primary
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper mapper;


    @Override
    public Director create(Director director) {
        log.debug("Запрос создать нового режиссёра");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        return director.withId(directorId);
    }

    @Override
    public Director update(Director director) {
        log.debug("Запрос обновить режиссёра с id {}.", director.getId());

        String sql = "UPDATE directors SET name = ? WHERE id = ?";

        int row = jdbcTemplate.update(sql,
                director.getName(),
                director.getId());

        if (row != 1) {
            log.error("Запрос обновить несуществующего режиссёра с id {}.", director.getId());
            throw new NotFoundException(String.format("Режиссёра с id %d не существует.", director.getId()));
        } else return director;
    }

    @Override
    public Director delete(Long id) {
        log.debug("Запрос удалить режиссёра по id {}.", id);

        Director director = getById(id);

        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return director;
    }

    @Override
    public List<Director> findAll() {
        log.debug("Запрос получить список всех режиссёров.");

        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, mapper::makeDirector);
    }

    @Override
    public Director getById(Long id) {
        log.debug("Запрос получить режиссёра по id {}.", id);

        String sql = "SELECT * FROM directors WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sql, mapper::makeDirector, id);
        if (directors.isEmpty()) {
            log.error("Запрос получить режиссёра по неверному id {}.", id);
            throw new NotFoundException(String.format("Режиссёра с id %d не существует.", id));
        } else return directors.get(0);
    }

    @Override
    public Set<Director> getFilmDirectors(Long filmId) {
        log.debug("Запрос получить список режиссёров фильма с id {}", filmId);

        String sql = "SELECT * FROM directors WHERE id IN "
                + "(SELECT director_id FROM film_directors WHERE film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sql, mapper::makeDirector, filmId));
    }

    @Override
    public void addFilmDirectors(Long filmId, Set<Director> directors) {
        log.debug("Запрос добавить режиссёров фильма с id {}", filmId);

        List<Director> filmDirectors = new ArrayList<>(directors);
        String sql = "INSERT INTO film_directors (film_id, director_id)"
                + "VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, filmDirectors.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return filmDirectors.size();
            }
        });
    }

    @Override
    public void updateFilmDirectors(Long filmId, Set<Director> directors) {
        log.debug("Запрос обновить режиссёров фильма с id {}", filmId);

        // Удаление прежнего списка режиссёров
        String delSql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(delSql, filmId);

        // Добавление новых режиссёров
        addFilmDirectors(filmId, directors);
    }

    @Override
    public void checkDirectors(Set<Director> directors) {
        log.debug("Запрос проверить наличие режиссёров.");

        List<Long> directorIdList = directors.stream()
                .map(Director::getId)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(directorIdList.size(), "?"));
        String sql = String.format("SELECT id FROM directors WHERE id IN (%s)", inSql);


        List<Long> directorDbIdList = jdbcTemplate.query(sql, directorIdList.toArray(),
                (rs, rowNum) -> rs.getLong("id"));
        if(directorDbIdList.size() < directorIdList.size()) {
            directorDbIdList.forEach(directorDbIdList::remove);
            Long directorId = directorDbIdList.stream().findFirst().get();
            log.error("Режиссёра с id {} нет в БД.", directorId);
            throw new NotFoundException(String.format("Неверно указан id (%d) режиссёра.", directorId));
        }
    }
}
