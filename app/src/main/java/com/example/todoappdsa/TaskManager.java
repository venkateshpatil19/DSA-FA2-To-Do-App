package com.example.todoappdsa;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskManager {
    private TaskDatabaseHelper dbHelper;
    private List<Task> taskList; // Use a list to manage tasks manually

    public TaskManager(Context context) {
        dbHelper = new TaskDatabaseHelper(context);
        taskList = new ArrayList<>();

        // Initialize the task list from the database
        List<Task> tasksFromDb = dbHelper.getAllTasks();
        for (Task task : tasksFromDb) {
            addTaskManual(task); // Add tasks from the database to the list in priority order
        }
    }

    // Add a new task manually by inserting it at the correct position based on priority
    public void addTask(String name, int priority, Date deadline) {
        Task newTask = new Task(name, priority, deadline);
        addTaskManual(newTask);  // Add the task manually to the list
        dbHelper.addTask(newTask);  // Add the task to the database
    }

    // Manually add task into the list while keeping the list ordered by priority
    private void addTaskManual(Task newTask) {
        if (taskList.isEmpty()) {
            taskList.add(newTask);  // If list is empty, just add the task
        } else {
            // Find the right position to insert the task based on priority
            int insertIndex = 0;
            for (int i = 0; i < taskList.size(); i++) {
                Task currentTask = taskList.get(i);
                if (newTask.getPriority() < currentTask.getPriority()) {
                    // If new task has higher priority (lower number), insert here
                    insertIndex = i;
                    break;
                } else {
                    // If we reach the end of the list, insert at the end
                    insertIndex = taskList.size();
                }
            }
            taskList.add(insertIndex, newTask); // Insert task at the calculated position
        }
    }

    // Get all tasks (already ordered by priority)
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskList); // Return the task list
    }

    // Search for a task by name
    public Task searchTaskByName(String taskName) {
        // Iterate through the task list to find the task by name
        for (Task task : taskList) {
            if (task.getTaskName().equalsIgnoreCase(taskName)) {
                return task;
            }
        }
        return null; // Return null if task not found
    }

    // Delete a task by name
    public String deleteTaskByName(String taskName) {
        Task taskToRemove = null;
        // Find the task by name in the list
        for (Task task : taskList) {
            if (task.getTaskName().equalsIgnoreCase(taskName)) {
                taskToRemove = task;
                break;
            }
        }

        // Check for high-priority tasks pending
        List<Task> highPriorityTasks = getHighPriorityTasks();
        if (!highPriorityTasks.isEmpty() && taskToRemove != null && taskToRemove.getPriority() > highPriorityTasks.get(0).getPriority()) {
            StringBuilder message = new StringBuilder("Unable to delete task. Please complete the high priority tasks: ");
            for (Task highTask : highPriorityTasks) {
                message.append(highTask.getTaskName()).append(" (Priority: ").append(highTask.getPriority()).append("), ");
            }
            return message.toString();
        }

        if (taskToRemove != null) {
            taskList.remove(taskToRemove); // Remove the task from the list
            dbHelper.deleteTaskByName(taskName); // Also delete from the database
            return "Task deleted: " + taskName;
        }
        return "Task not found."; // Return false if task not found
    }

    // Get all high-priority tasks
    private List<Task> getHighPriorityTasks() {
        List<Task> highPriorityTasks = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getPriority() == 1) { // Assuming priority 1 is high
                highPriorityTasks.add(task);
            }
        }
        return highPriorityTasks;
    }
}
