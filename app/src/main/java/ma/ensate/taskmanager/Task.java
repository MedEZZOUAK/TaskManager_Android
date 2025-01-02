// Task.java
package ma.ensate.taskmanager;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private int categoryId;
    private boolean isCompleted;

    // Constructor, getters, and setters
    public Task(int id, String title, String description, String dueDate, String priority, int categoryId, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.categoryId = categoryId;
        this.isCompleted = isCompleted;
    }

  public Task() {

  }

  public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

  public void setCompleted(boolean completed) {
    isCompleted = completed;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDueDate(String dueDate) {
    this.dueDate = dueDate;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public void setCategoryId(int categoryId) {
    this.categoryId = categoryId;
  }
}
