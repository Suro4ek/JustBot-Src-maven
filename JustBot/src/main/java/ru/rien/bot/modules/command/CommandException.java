package ru.rien.bot.modules.command;

import java.io.PrintStream;
import java.io.PrintWriter;

public class CommandException extends RuntimeException {

    public CommandException() {}

    @Override
    public StackTraceElement[] getStackTrace() {
        return new StackTraceElement[0];
    }

    @Override
    public void printStackTrace() {

    }

    @Override
    public void printStackTrace(PrintStream s) {

    }

    @Override
    public void printStackTrace(PrintWriter s) {

    }

    @Override
    public String getMessage() {
        return "остановка команды, это не ошибка";
    }
}
