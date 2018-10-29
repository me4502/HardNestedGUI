package com.me4502.hardnestgui.broker.task;

public interface Task {

    /**
     * Gets the name of the task.
     *
     * @return The task name
     */
    String getName();

    /**
     * Runs the task, returning a new task to replace it with.
     *
     * @return The new task, or null.
     * @throws TaskException If the task failed unrecoverably
     */
    Task run() throws TaskException;
}
