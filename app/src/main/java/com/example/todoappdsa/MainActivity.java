package com.example.todoappdsa;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TaskManager taskManager;
    private EditText taskNameInput, taskPriorityInput, taskDeadlineInput, searchTaskInput;
    private LinearLayout taskDisplayLayout;
    private Button addTaskButton, searchTaskButton, viewAllTasksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TaskManager
        taskManager = new TaskManager(this);

        // Initialize UI elements
        taskNameInput = findViewById(R.id.taskNameInput);
        taskPriorityInput = findViewById(R.id.taskPriorityInput);
        taskDeadlineInput = findViewById(R.id.taskDeadlineInput);
        searchTaskInput = findViewById(R.id.searchTaskInput);
        taskDisplayLayout = findViewById(R.id.taskDisplayLayout);
        addTaskButton = findViewById(R.id.addTaskButton);
        searchTaskButton = findViewById(R.id.searchTaskButton);
        viewAllTasksButton = findViewById(R.id.viewAllTasksButton);

        // DatePickerDialog for task deadline
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            taskDeadlineInput.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        taskDeadlineInput.setOnClickListener(v -> datePickerDialog.show());

        // Add Task button click
        addTaskButton.setOnClickListener(v -> {
            String taskName = taskNameInput.getText().toString();
            int priority;
            try {
                priority = Integer.parseInt(taskPriorityInput.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid priority number.", Toast.LENGTH_SHORT).show();
                return;
            }

            String deadlineString = taskDeadlineInput.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date deadline = null;
            try {
                deadline = sdf.parse(deadlineString);
            } catch (ParseException e) {
                Toast.makeText(MainActivity.this, "Invalid deadline format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (deadline != null) {
                taskManager.addTask(taskName, priority, deadline);
                Toast.makeText(MainActivity.this, "Task added: " + taskName, Toast.LENGTH_SHORT).show();
                taskDisplayLayout.removeAllViews(); // Clear existing views
            }
        });

        // Search Task button click
        searchTaskButton.setOnClickListener(v -> {
            String taskName = searchTaskInput.getText().toString();
            displayTaskWithCheckbox(taskName);
        });

        // View All Tasks button click
        viewAllTasksButton.setOnClickListener(v -> displayAllTasks());
    }

    private void displayTaskWithCheckbox(String taskName) {
        taskDisplayLayout.removeAllViews(); // Clear previous results
        Task task = taskManager.searchTaskByName(taskName);
        if (task != null) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(task.getTaskName());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    String deleteMessage = taskManager.deleteTaskByName(task.getTaskName());
                    Toast.makeText(MainActivity.this, deleteMessage, Toast.LENGTH_LONG).show();
                    if (deleteMessage.contains("Unable to delete task")) {
                        checkBox.setChecked(false); // Uncheck if unable to delete
                    }
                }
            });
            taskDisplayLayout.addView(checkBox);
        } else {
            Toast.makeText(MainActivity.this, "Task not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAllTasks() {
        taskDisplayLayout.removeAllViews(); // Clear previous views
        List<Task> allTasks = taskManager.getAllTasks(); // Get all tasks from the task manager
        if (allTasks.isEmpty()) {
            Toast.makeText(MainActivity.this, "No tasks available.", Toast.LENGTH_SHORT).show();
        } else {
            for (Task task : allTasks) {
                CheckBox checkBox = new CheckBox(this);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Date format
                String deadlineFormatted = sdf.format(task.getDeadline());
                checkBox.setText(task.getTaskName() + " (Priority: " + task.getPriority() + ", Deadline: " + deadlineFormatted + ")");
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        String deleteMessage = taskManager.deleteTaskByName(task.getTaskName());
                        if (deleteMessage.contains("Unable to delete task")) {
                            Toast.makeText(MainActivity.this, deleteMessage, Toast.LENGTH_LONG).show();
                            checkBox.setChecked(false);
                        } else {
                            Toast.makeText(MainActivity.this, deleteMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                taskDisplayLayout.addView(checkBox); // Add each task as a checkbox
            }
        }
    }


}
