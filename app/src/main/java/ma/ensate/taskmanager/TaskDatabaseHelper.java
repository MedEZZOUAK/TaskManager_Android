package ma.ensate.taskmanager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "tasks.db";
  private static final int DATABASE_VERSION = 1;

  // Tasks table
  private static final String TABLE_TASKS = "tasks";
  private static final String COLUMN_ID = "id";
  private static final String COLUMN_TITLE = "title";
  private static final String COLUMN_DESCRIPTION = "description";
  private static final String COLUMN_DUE_DATE = "due_date";
  private static final String COLUMN_PRIORITY = "priority";
  private static final String COLUMN_CATEGORY_ID = "category_id";
  private static final String COLUMN_IS_COMPLETED = "is_completed";

  // Categories table
  private static final String TABLE_CATEGORIES = "categories";
  private static final String COLUMN_CATEGORY_NAME = "name";

  public TaskDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTasksTable = "CREATE TABLE " + TABLE_TASKS + "(" +
      COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      COLUMN_TITLE + " TEXT," +
      COLUMN_DESCRIPTION + " TEXT," +
      COLUMN_DUE_DATE + " TEXT," +
      COLUMN_PRIORITY + " TEXT," +
      COLUMN_CATEGORY_ID + " INTEGER," +
      COLUMN_IS_COMPLETED + " INTEGER," +
      "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
      ");";

    String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + "(" +
      COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
      COLUMN_CATEGORY_NAME + " TEXT" +
      ");";

    db.execSQL(createTasksTable);
    db.execSQL(createCategoriesTable);
    insertDefaultCategories(db);
  }

  private void insertDefaultCategories(SQLiteDatabase db) {
    String[] defaultCategories = {"Work", "Personal", "Studies"};
    for (String category : defaultCategories) {
      ContentValues values = new ContentValues();
      values.put(COLUMN_CATEGORY_NAME, category);
      db.insert(TABLE_CATEGORIES, null, values);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
    onCreate(db);
  }

  // CRUD methods for tasks
  public void addTask(String title, String description, String dueDate, String priority, int categoryId, int isCompleted) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_TITLE, title);
    values.put(COLUMN_DESCRIPTION, description);
    values.put(COLUMN_DUE_DATE, dueDate);
    values.put(COLUMN_PRIORITY, priority);
    values.put(COLUMN_CATEGORY_ID, categoryId);
    values.put(COLUMN_IS_COMPLETED, isCompleted);
    db.insert(TABLE_TASKS, null, values);
  }

  public List<Task> getAllTasks() {
    List<Task> tasks = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

    if (cursor.moveToFirst()) {
      do {
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
        int dueDateIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
        int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
        int categoryIdIndex = cursor.getColumnIndex(COLUMN_CATEGORY_ID);
        int isCompletedIndex = cursor.getColumnIndex(COLUMN_IS_COMPLETED);

        if (idIndex >= 0 && titleIndex >= 0 && descriptionIndex >= 0 && dueDateIndex >= 0 &&
          priorityIndex >= 0 && categoryIdIndex >= 0 && isCompletedIndex >= 0) {

          int id = cursor.getInt(idIndex);
          String title = cursor.getString(titleIndex);
          String description = cursor.getString(descriptionIndex);
          String dueDate = cursor.getString(dueDateIndex);
          String priority = cursor.getString(priorityIndex);
          int categoryId = cursor.getInt(categoryIdIndex);
          boolean isCompleted = cursor.getInt(isCompletedIndex) == 1;

          Task task = new Task(id, title, description, dueDate, priority, categoryId, isCompleted);
          tasks.add(task);
          Log.d("TaskDatabaseHelper", "Task retrieved: " + task.getTitle());
        }
      } while (cursor.moveToNext());
    }

    cursor.close();
    return tasks;
  }

  public int updateTask(int id, String title, String description, String dueDate, String priority, int categoryId, int isCompleted) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_TITLE, title);
    values.put(COLUMN_DESCRIPTION, description);
    values.put(COLUMN_DUE_DATE, dueDate);
    values.put(COLUMN_PRIORITY, priority);
    values.put(COLUMN_CATEGORY_ID, categoryId);
    values.put(COLUMN_IS_COMPLETED, isCompleted);
    return db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  public int deleteTask(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  // CRUD methods for categories
// Add logging in addCategory method
  public void addCategory(String name) {
    //check if the category already exists
    if (getAllCategories().contains(name)) {
      Log.d("TaskDatabaseHelper", "Category already exists: " + name);
      return;
    }

    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_CATEGORY_NAME, name);
    db.insert(TABLE_CATEGORIES, null, values);
    Log.d("TaskDatabaseHelper", "Category added: " + name);
  }

  // Add logging in getAllCategories method
  @SuppressLint("Range")
  public List<String> getAllCategories() {
    List<String> categories = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT name FROM " + TABLE_CATEGORIES, null);

    if (cursor.moveToFirst()) {
      do {
        String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME));
        categories.add(category);
        Log.d("TaskDatabaseHelper", "Category retrieved: " + category);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return categories;
  }

  public int updateCategory(int id, String name) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_CATEGORY_NAME, name);
    return db.update(TABLE_CATEGORIES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  public int deleteCategory(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(TABLE_CATEGORIES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  public int getCompletedTasksCount() {
    String query = "SELECT COUNT(*) FROM tasks WHERE is_completed = 1";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(query, null);
    cursor.moveToFirst();
    int count = cursor.getInt(0);
    cursor.close();
    return count;
  }

  // Similarly, add getOngoingTasksCount() and getOverdueTasksCount()
  public int getOngoingTasksCount() {
    String query = "SELECT COUNT(*) FROM tasks WHERE is_completed = 0";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(query, null);
    cursor.moveToFirst();
    int count = cursor.getInt(0);
    cursor.close();
    return count;
  }

  public int getOverdueTasksCount() {
    String query = "SELECT COUNT(*) FROM tasks WHERE is_completed = 0 AND due_date < date('now')";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(query, null);
    cursor.moveToFirst();
    int count = cursor.getInt(0);
    cursor.close();
    return count;
  }

  public void markTaskAsCompleted(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_IS_COMPLETED, 1);
    db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  public void markTaskAsOngoing(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_IS_COMPLETED, 0);
    db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  public int getIdFromCategoryName(String categoryName) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_CATEGORIES + " WHERE name = ?", new String[]{categoryName});
    cursor.moveToFirst();
    int id = cursor.getInt(0);
    cursor.close();
    return id;
  }

  public List<Task> getTasksByCategory(int categoryId) {
    List<Task> tasks = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(categoryId)});

    if (cursor.moveToFirst()) {
      do {
        Task task = getTaskFromCursor(cursor);
        tasks.add(task);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return tasks;
  }

  public List<Task> getTasksByPriority(String priority) {
    List<Task> tasks = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_PRIORITY + " = ?", new String[]{priority});

    if (cursor.moveToFirst()) {
      do {
        Task task = getTaskFromCursor(cursor);
        tasks.add(task);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return tasks;
  }

  public List<Task> getTasksByDate(String date) {
    List<Task> tasks = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_DUE_DATE + " = ?", new String[]{date});

    if (cursor.moveToFirst()) {
      do {
        Task task = getTaskFromCursor(cursor);
        tasks.add(task);
      } while (cursor.moveToNext());
    }

    cursor.close();
    return tasks;
  }

  private Task getTaskFromCursor(Cursor cursor) {
    @SuppressLint("Range") int idIndex = cursor.getColumnIndex(COLUMN_ID);
    @SuppressLint("Range") int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
    @SuppressLint("Range") int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
    @SuppressLint("Range") int dueDateIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
    @SuppressLint("Range") int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
    @SuppressLint("Range") int categoryIdIndex = cursor.getColumnIndex(COLUMN_CATEGORY_ID);
    @SuppressLint("Range") int isCompletedIndex = cursor.getColumnIndex(COLUMN_IS_COMPLETED);

    if (idIndex >= 0 && titleIndex >= 0 && descriptionIndex >= 0 && dueDateIndex >= 0 &&
      priorityIndex >= 0 && categoryIdIndex >= 0 && isCompletedIndex >= 0) {

      int id = cursor.getInt(idIndex);
      String title = cursor.getString(titleIndex);
      String description = cursor.getString(descriptionIndex);
      String dueDate = cursor.getString(dueDateIndex);
      String priority = cursor.getString(priorityIndex);
      int categoryId = cursor.getInt(categoryIdIndex);
      boolean isCompleted = cursor.getInt(isCompletedIndex) == 1;

      return new Task(id, title, description, dueDate, priority, categoryId, isCompleted);
    } else {
      // Handle the case where one or more columns are missing
      throw new IllegalArgumentException("Cursor does not contain all required columns");
    }
  }

  public int updateTask(int id, String title, String description, String dueDate, String priority, int categoryId, boolean isCompleted) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_TITLE, title);
    values.put(COLUMN_DESCRIPTION, description);
    values.put(COLUMN_DUE_DATE, dueDate);
    values.put(COLUMN_PRIORITY, priority);
    values.put(COLUMN_CATEGORY_ID, categoryId);
    values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
    return db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
  }

  public List<Task> getCloseTasks() {
    List<Task> closeTasks = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    String query = "SELECT * FROM tasks WHERE due_date <= datetime('now', '+1 day')";
    Cursor cursor = db.rawQuery(query, null);

    if (cursor.moveToFirst()) {
      do {
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        int dueDateIndex = cursor.getColumnIndex(COLUMN_DUE_DATE);
        int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
        int categoryIdIndex = cursor.getColumnIndex(COLUMN_CATEGORY_ID);
        int isCompletedIndex = cursor.getColumnIndex(COLUMN_IS_COMPLETED);

        if (idIndex >= 0 && titleIndex >= 0 && dueDateIndex >= 0 &&
          priorityIndex >= 0 && categoryIdIndex >= 0 && isCompletedIndex >= 0) {

          Task task = new Task();
          task.setId(cursor.getInt(idIndex));
          task.setTitle(cursor.getString(titleIndex));
          task.setDueDate(cursor.getString(dueDateIndex));
          task.setPriority(cursor.getString(priorityIndex));
          task.setCategoryId(cursor.getInt(categoryIdIndex));
          task.setCompleted(cursor.getInt(isCompletedIndex) == 1);
          closeTasks.add(task);
        }
      } while (cursor.moveToNext());
    }
    cursor.close();
    db.close();
    return closeTasks;
  }
}
