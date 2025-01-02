// Add_TASK.java
package ma.ensate.taskmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class Add_TASK extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_add_task);

    Spinner categories = findViewById(R.id.spinner_category);
    Spinner priorities = findViewById(R.id.spinner_priority);
    Button addTaskButton = findViewById(R.id.button_save_task);
    EditText Date = findViewById(R.id.edit_due_date);
    EditText titleEditText = findViewById(R.id.edit_title);
    EditText descriptionEditText = findViewById(R.id.edit_description);

    // Populate the priorities spinner (High, Medium, Low)
    String[] prioritiesArray = {"High", "Medium", "Low"};
    ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prioritiesArray);
    priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    priorities.setAdapter(priorityAdapter);

    // Populate the categories spinner
    TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(this);
    List<String> categoriesList = dbHelper.getAllCategories();
    Log.d("Add_TASK", "onCreate: " + categoriesList.toString());

    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    categories.setAdapter(categoryAdapter);

    addTaskButton.setOnClickListener(v -> {
      String title = titleEditText.getText().toString();
      String description = descriptionEditText.getText().toString();
      String dueDate = Date.getText().toString();
      String priority = priorities.getSelectedItem().toString();
      String category = categories.getSelectedItem().toString();
      int categoryId = dbHelper.getIdFromCategoryName(category);
      Log.d("Add_TASK", "Task added details: " + title + ", " + description + ", " + dueDate + ", " + priority + ", " + category + ", " + categoryId);
      dbHelper.addTask(title, description, dueDate, priority, categoryId, 0);
      finish();
    });

    Date.setOnClickListener(v -> {
      final Calendar calendar = Calendar.getInstance();
      int year = calendar.get(Calendar.YEAR);
      int month = calendar.get(Calendar.MONTH);
      int day = calendar.get(Calendar.DAY_OF_MONTH);

      DatePickerDialog datePickerDialog = new DatePickerDialog(Add_TASK.this, (view, year1, month1, dayOfMonth) -> {
        // Set the selected date to the EditText
        Date.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth);
      }, year, month, day);

      // Set the minimum date to the current date
      datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

      // Show the date picker dialog
      datePickerDialog.show();
    });
  }
}
