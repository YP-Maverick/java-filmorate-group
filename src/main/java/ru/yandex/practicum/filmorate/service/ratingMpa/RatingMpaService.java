package ru.yandex.practicum.filmorate.service.ratingMpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingMpaStorage;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@AllArgsConstructor
@Service
public class RatingMpaService {
    private final RatingMpaStorage ratingStorage;

    public RatingMpa getRatingById(int id) {
        return ratingStorage.getRatingById(id);
    }

    public List<RatingMpa> getAllRatings() {
        return ratingStorage.getAllRatings();
    }
}
