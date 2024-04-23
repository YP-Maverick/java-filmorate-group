package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {FilmController.class,
        UserController.class,
        GenreController.class,
        RatingMpaController.class})
public class ErrorHandler {
    private void log(Throwable e) {
        log.error("Исключение {}: {}", e, e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final ValidationException e) {
        log(e);
        return Map.of("error", "Validation error",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log(e);
        return Map.of("error", "Object is not found",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler({LikeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleLikeExc(final LikeException e) {
        log(e);
        return Map.of("error", "Like error",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler({GenreException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleGenreExc(final GenreException e) {
        log(e);
        return Map.of("error", "Error with genre",
                "errorMessage", e.getMessage());
    }

    @ExceptionHandler({RatingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRatingExc(final RatingException e) {
        log(e);
        return Map.of("error", "Error with MPA",
                "errorMessage", e.getMessage());
    }
}
