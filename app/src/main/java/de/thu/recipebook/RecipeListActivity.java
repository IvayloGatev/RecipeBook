package de.thu.recipebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeRepository recipeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recipeRepository = new RecipeRepository();
        List<Recipe> recipes = recipeRepository.getAllRecipes();
        List<String> recipeNames = new ArrayList<>();
        for (Recipe recipe : recipes) {
            recipeNames.add(recipe.getName());
        }

        //Code for exercise 2
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                R.layout.list_view_item_recipe,
//                R.id.text_view_recipe_list,
//                recipeNames);

        RecipeListAdapter adapter = new RecipeListAdapter(recipeRepository.getAllRecipes());
        ListView listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);
    }
}