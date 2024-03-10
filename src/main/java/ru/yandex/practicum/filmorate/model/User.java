package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
    private final int id;
    @Email(message = "Email не корректный")
    @NotBlank(message = "Email пустой")
    private final String email;
    @NotBlank(message = "Login пустой")
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        if(name ==null || name.isBlank()) {
            this.name = login;
        } else this.name = name;
        this.birthday = birthday;
    }
}
