package de.thu.recipebook.activities;

import android.os.Bundle;
import android.widget.ListView;

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
    }
}