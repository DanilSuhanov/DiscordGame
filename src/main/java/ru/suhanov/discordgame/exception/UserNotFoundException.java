package ru.suhanov.discordgame.exception;

public class UserNotFoundException extends DataBaseException {
    public UserNotFoundException() {
        super("Пользователь не найден!");
    }
}
