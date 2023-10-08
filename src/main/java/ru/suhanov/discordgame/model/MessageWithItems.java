package ru.suhanov.discordgame.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class MessageWithItems {
    private String message;
    private String url;
    private List<Button> buttons = new ArrayList<>();
    private SelectMenu selectMenu;

    public void addButton(Button button) {
        buttons.add(button);
    }

    public void addButtons(List<Button> buttons) {
        this.buttons.addAll(buttons);
    }

    public MessageWithItems(String message) {
        this.message = message;
    }

    public MessageWithItems(String message, List<Button> buttons) {
        this.message = message;
        this.buttons = buttons;
    }

    public MessageWithItems(String message, List<Button> buttons, String url) {
        this.message = message;
        this.buttons = buttons;
        this.url = url;
    }
}
