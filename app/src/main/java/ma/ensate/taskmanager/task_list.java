package ma.ensate.taskmanager;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class task_list extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        recyclerView = findViewById(R.id.recycler_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize task list and adapter
        taskList = new ArrayList<>();
        dbHelper = new TaskDatabaseHelper(this);
        taskAdapter = new TaskAdapter(taskList, dbHelper);
        recyclerView.setAdapter(taskAdapter);

        // Load tasks from the database
        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}
