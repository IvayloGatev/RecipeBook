package de.thu.recipebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeRepository recipeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = findViewById(R.id.toolbar_recipe_list);
        setSupportActionBar(toolbar);

        recipeRepository = RecipeRepository.getInstance();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Recipe recipe = recipes.get(i);
                Intent recipeDetailsIntent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
                recipeDetailsIntent.putExtra("name", recipe.getName());
                recipeDetailsIntent.putExtra("country", recipe.getCountry());
                recipeDetailsIntent.putExtra("ingredients", recipe.getIngredients());
                recipeDetailsIntent.putExtra("instructions", recipe.getInstructions());
                startActivity(recipeDetailsIntent);
            }
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
            case R.id.create_recipe_entry:
                Intent createRecipeIntent = new Intent(this, CreateRecipeActivity.class);
                startActivity(createRecipeIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}