package ma.ensate.taskmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class category_management extends AppCompatActivity implements CategoryAdapter.OnCategoryDeleteListener {

    private TaskDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private EditText editTextCategoryName;
    private Button buttonAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        dbHelper = new TaskDatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextCategoryName = findViewById(R.id.edit_text_category_name);
        buttonAddCategory = findViewById(R.id.button_add_category);

        buttonAddCategory.setOnClickListener(v -> {
            String categoryName = editTextCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                dbHelper.addCategory(categoryName);
                loadCategories();
                editTextCategoryName.setText("");
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        List<String> categoryNames = dbHelper.getAllCategories();
        categoryList = new ArrayList<>();
        for (String name : categoryNames) {
            int id = dbHelper.getIdFromCategoryName(name);
            categoryList.add(new Category(id, name));
        }

        categoryAdapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(categoryAdapter);
    }

    @Override
    public void onCategoryDelete(int categoryId) {
        dbHelper.deleteCategory(categoryId);
        loadCategories();
    }
}
