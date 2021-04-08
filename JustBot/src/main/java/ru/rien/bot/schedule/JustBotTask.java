package ru.rien.bot.schedule;

public abstract class JustBotTask implements Runnable {

    private String taskName;

    private JustBotTask() {
    }

    public JustBotTask(String taskName) {
        this.taskName = taskName;
    }

    public JustBotTask(String taskName, long delay) {
        this.taskName = taskName;
        delay(delay);
    }

    public JustBotTask(String taskName, long delay, long interval) {
        this.taskName = taskName;
        repeat(delay, interval);
    }

    public boolean repeat(long delay, long interval) {
        return Scheduler.scheduleRepeating(this, taskName, delay, interval);
    }

    public void delay(long delay) {
        Scheduler.delayTask(this, taskName, delay);
    }

    public boolean cancel() {
        return Scheduler.cancelTask(taskName);
    }

}