package de.thu.recipebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeRepository recipeRepository;
    private RecipeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = findViewById(R.id.toolbar_recipe_list);
        setSupportActionBar(toolbar);

        recipeRepository = RecipeRepository.getInstance();
        List<Recipe> recipes = recipeRepository.getAllRecipes();

        //Code for exercise 2
//        List<String> recipeNames = new ArrayList<>();
//        for (Recipe recipe : recipes) {
//            recipeNames.add(recipe.getName());
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                R.layout.list_view_item_recipe,
//                R.id.text_view_recipe_list,
//                recipeNames);

        adapter = new RecipeListAdapter(recipes);
        ListView listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Recipe recipe = recipeRepository.getRecipe(i);
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
            case R.id.refresh_entry:
                adapter.setData(recipeRepository.getAllRecipes());
                adapter.notifyDataSetChanged();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}