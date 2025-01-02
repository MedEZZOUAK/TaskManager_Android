package ma.ensate.taskmanager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_POST_NOTIFICATIONS = 1;
    private TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);
        displayTaskStatistics();
        Button addTaskButton = findViewById(R.id.button_add_task);
        Button viewTasksButton = findViewById(R.id.button_view_tasks);
        Button manageCategoriesButton = findViewById(R.id.button_manage_categories);

        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Add_TASK.class);
            startActivity(intent);
        });

        viewTasksButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, task_list.class);
            startActivity(intent);
        });

        manageCategoriesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, category_management.class);
            startActivity(intent);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
            } else {
                scheduleTaskReminder();
            }
        } else {
            scheduleTaskReminder();
        }
    }

    @Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_POST_NOTIFICATIONS) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scheduleTaskReminder();
        } else {
            Log.e("MainActivity", "Permission for POST_NOTIFICATIONS was denied.");
        }
    }
}

    private void displayTaskStatistics() {
        int completedTasks = dbHelper.getCompletedTasksCount();
        int ongoingTasks = dbHelper.getOngoingTasksCount();
        int overdueTasks = dbHelper.getOverdueTasksCount();

        TextView tvCompleted = findViewById(R.id.text_completed_tasks_count);
        TextView tvOngoing = findViewById(R.id.text_ongoing_tasks_count);
        TextView tvOverdue = findViewById(R.id.text_overdue_tasks_count);

        tvCompleted.setText(String.valueOf(completedTasks));
        tvOngoing.setText(String.valueOf(ongoingTasks));
        tvOverdue.setText(String.valueOf(overdueTasks));
    }

    private void scheduleTaskReminder() {
    Intent intent = new Intent(this, TaskReminderReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.set(Calendar.HOUR_OF_DAY, 9);

    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY, pendingIntent);
}
    @Override
    protected void onResume() {
        super.onResume();
        displayTaskStatistics();
    }
}
