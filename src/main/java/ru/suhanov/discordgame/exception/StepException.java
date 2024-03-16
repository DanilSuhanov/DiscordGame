package ru.suhanov.discordgame.exception;

public class StepException extends Exception {
    public StepException() {
        super("Попытка действия не в свой ход!");
    }
}
