package com.me4502.hardnestgui.broker.task;

/**
 * An exception that occured while running a task.
 */
public class TaskException extends Exception {

    private Task task;

    public TaskException(Task task, String message) {
        super(message);
        this.task = task;
    }

    /**
     * Get the task that triggered the error.
     *
     * @return The task
     */
    public Task getTask() {
        return task;
    }
}
