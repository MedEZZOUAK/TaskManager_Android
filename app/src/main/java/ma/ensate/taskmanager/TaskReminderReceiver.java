package ma.ensate.taskmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "task_reminder_channel";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            createNotificationChannel(context);
            TaskDatabaseHelper dbHelper = new TaskDatabaseHelper(context);
            List<Task> closeTasks = dbHelper.getCloseTasks();

            if (!closeTasks.isEmpty()) {
                StringBuilder taskDetails = new StringBuilder();
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                Date currentDate = new Date();

                for (Task task : closeTasks) {
                    try {
                        Date taskDate = dateFormat.parse(task.getDueDate());
                        if (taskDate != null && taskDate.before(currentDate)) {
                            taskDetails.append(task.getTitle()).append("\n");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (taskDetails.length() > 0) {
                    Intent notificationIntent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.notif)
                            .setContentTitle("Close Task Reminder")
                            .setContentText("You have tasks nearing their deadline:\n" + taskDetails.toString())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                }
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminder Channel";
            String description = "Channel for task reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
