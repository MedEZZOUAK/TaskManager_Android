package ma.ensate.taskmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class UpdateTask extends AppCompatActivity {

    private TaskDatabaseHelper dbHelper;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        Spinner categories = findViewById(R.id.spinner_category);
        Spinner priorities = findViewById(R.id.spinner_priority);
        Button updateTaskButton = findViewById(R.id.button_update_task);
        EditText dateEditText = findViewById(R.id.edit_due_date);
        EditText titleEditText = findViewById(R.id.edit_task_title);
        EditText descriptionEditText = findViewById(R.id.edit_task_description);

        dbHelper = new TaskDatabaseHelper(this);

        // Populate the priorities spinner (High, Medium, Low)
        String[] prioritiesArray = {"High", "Medium", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prioritiesArray);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorities.setAdapter(priorityAdapter);

        // Populate the categories spinner
        List<String> categoriesList = dbHelper.getAllCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(categoryAdapter);

        // Get task details from intent
        taskId = getIntent().getIntExtra("Task_id", -1);
        String taskTitle = getIntent().getStringExtra("TASK_TITLE");
        String taskDescription = getIntent().getStringExtra("TASK_DESCRIPTION");
        String taskDueDate = getIntent().getStringExtra("TASK_DUE_DATE");
        String taskPriority = getIntent().getStringExtra("TASK_PRIORITY");

        // Set task details to input fields
        titleEditText.setText(taskTitle);
        descriptionEditText.setText(taskDescription);
        dateEditText.setText(taskDueDate);
        priorities.setSelection(priorityAdapter.getPosition(taskPriority));

        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateTask.this, (view, year1, month1, dayOfMonth) -> {
                dateEditText.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth);
            }, year, month, day);

            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        updateTaskButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String dueDate = dateEditText.getText().toString();
            String priority = priorities.getSelectedItem().toString();
            String category = categories.getSelectedItem().toString();
            int categoryId = dbHelper.getIdFromCategoryName(category);

            dbHelper.updateTask(taskId, title, description, dueDate, priority, categoryId, false);
            finish();
        });
    }
}
