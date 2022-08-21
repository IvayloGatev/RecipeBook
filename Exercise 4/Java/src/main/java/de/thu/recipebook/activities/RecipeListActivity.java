package de.thu.recipebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;
import de.thu.recipebook.models.RecipeListAdapter;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeCollection recipeCollection;
    private RecipeListAdapter adapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recipeCollection = RecipeCollection.getInstance();
        adapter = new RecipeListAdapter();
        List<Recipe> data = recipeCollection.getRecipes();
        adapter.setData(data);

        listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent recipeDetailsIntent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
            recipeDetailsIntent.putExtra("id", i);
            startActivity(recipeDetailsIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_recipe_entry:
                Intent createRecipeIntent = new Intent(this, SaveRecipeActivity.class);
                startActivity(createRecipeIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}