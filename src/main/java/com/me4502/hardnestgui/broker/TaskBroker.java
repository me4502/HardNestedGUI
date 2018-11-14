package com.me4502.hardnestgui.broker;

import com.me4502.hardnestgui.app.HardNestedApplication;
import com.me4502.hardnestgui.broker.task.KeyFindTask;
import com.me4502.hardnestgui.broker.task.KeyValidateTask;
import com.me4502.hardnestgui.broker.task.Task;
import com.me4502.hardnestgui.broker.task.TaskException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskBroker implements Runnable {

    private Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    private Task currentTask;

    public void startTasks() {
        taskQueue.add(new KeyValidateTask());
        taskQueue.add(new KeyFindTask());
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (currentTask != null || !taskQueue.isEmpty()) {
                    if (currentTask == null) {
                        currentTask = taskQueue.poll();
                    }
                    currentTask = currentTask.run();
                }
            } catch (TaskException e) {
                e.printStackTrace();
                taskQueue.clear();
                currentTask = null;
                HardNestedApplication.getInstance().failWithError("Task '" + e.getTask().getName() + "' failed with message: " + e.getMessage());
            }
        }
    }
}
