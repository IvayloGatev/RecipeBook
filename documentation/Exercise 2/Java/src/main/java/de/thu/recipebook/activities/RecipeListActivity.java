package de.thu.recipebook.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeCollection recipeCollection;
    private ArrayAdapter<String> adapter;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recipeCollection = RecipeCollection.getInstance();
        List<String> recipeNames = new ArrayList<>();
        for (Recipe recipe : recipeCollection.getRecipes()) {
            recipeNames.add(recipe.getName());
        }

        adapter = new ArrayAdapter<>(this,
                R.layout.list_view_item_recipe,
                R.id.text_view_recipe,
                recipeNames);
        listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);
    }
}