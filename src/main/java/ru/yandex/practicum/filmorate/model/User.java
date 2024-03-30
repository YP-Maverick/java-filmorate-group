package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import ru.yandex.practicum.filmorate.validation.NotContainsSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Value
@Builder
public class User {
    @With
    Long id;
    @Email(message = "Электронная почта должна иметь формат адреса электронной почты")
    @NotBlank(message = "Электронная почта не должна быть пустой")
    String email;
    @NotBlank(message = "Логин не должен быть пустым")
    @NotContainsSpaces(message = "Логин не должен содержать пробелы")
    String login;
    String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        if (name == null || name.isBlank()) {
            this.name = login;
        } else this.name = name;
        this.birthday = birthday;
    }
}
