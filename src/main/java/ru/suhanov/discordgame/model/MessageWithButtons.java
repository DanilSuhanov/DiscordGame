package ru.suhanov.discordgame.model;

import lombok.Data;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.LinkedList;
import java.util.List;

@Data
public class MessageWithButtons {
    private String message;
    private String url;
    private List<Button> buttons = new LinkedList<>();

    public void addButton(Button button) {
        buttons.add(button);
    }

    public MessageWithButtons(String message) {
        this.message = message;
    }

    public MessageWithButtons(String message, List<Button> buttons) {
        this.message = message;
        this.buttons = buttons;
    }

    public MessageWithButtons(String message, List<Button> buttons, String url) {
        this.message = message;
        this.buttons = buttons;
        this.url = url;
    }
}
