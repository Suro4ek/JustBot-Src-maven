package ru.rien.bot.utils;

import java.awt.*;

public enum MessageType {

    INFO(Color.CYAN),
    SUCCESS(Color.GREEN),
    WARNING(Color.YELLOW),
    MODERATION(Color.WHITE),
    ERROR(Color.RED),
    NEUTRAL(Color.CYAN);

    private final Color color;

    MessageType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}