// TaskAdapter.java
package ma.ensate.taskmanager;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private TaskDatabaseHelper dbHelper;

    public TaskAdapter(List<Task> tasks, TaskDatabaseHelper dbHelper) {
        this.tasks = tasks;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getDescription());
        holder.dueDate.setText(task.getDueDate());
        holder.priority.setText(task.getPriority());
        holder.checkCompleted.setChecked(task.isCompleted());

        holder.checkCompleted.setOnClickListener(v -> {
            boolean isChecked = holder.checkCompleted.isChecked();
            dbHelper.markTaskAsCompleted(task.getId());
            task.setCompleted(isChecked);
        });

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteTask(task.getId());
            tasks.remove(position);
            notifyItemRemoved(position);
        });
        holder.itemView.setOnClickListener(v -> {
          Intent intent = new Intent(v.getContext(), UpdateTask.class);
          intent.putExtra("Task_id", task.getId());
          intent.putExtra("TASK_TITLE", task.getTitle());
          intent.putExtra("TASK_DESCRIPTION", task.getDescription());
          intent.putExtra("TASK_DUE_DATE", task.getDueDate());
          intent.putExtra("TASK_PRIORITY", task.getPriority());
          intent.putExtra("TASK_COMPLETED", task.isCompleted());
          v.getContext().startActivity(intent);
          });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDescription, dueDate, priority;
        CheckBox checkCompleted;
        Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.text_task_title);
            taskDescription = itemView.findViewById(R.id.text_task_description);
            dueDate = itemView.findViewById(R.id.text_due_date);
            priority = itemView.findViewById(R.id.text_priority);
            checkCompleted = itemView.findViewById(R.id.check_completed);
            deleteButton = itemView.findViewById(R.id.button_delete_task);
        }
    }
}
