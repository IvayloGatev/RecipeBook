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
    private FetchRecipeListRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

//        Exercise 5
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = findViewById(R.id.toolbar_recipe_list);
        setSupportActionBar(toolbar);

        recipeRepository = RecipeRepository.getInstance();
        adapter = new RecipeListAdapter();
        runnable = new FetchRecipeListRunnable(recipeRepository, this, adapter);
        new Thread(runnable).start();

        //Code for exercise 2
//        List<String> recipeNames = new ArrayList<>();
//        for (Recipe recipe : recipes) {
//            recipeNames.add(recipe.getName());
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                R.layout.list_view_item_recipe,
//                R.id.text_view_recipe_list,
//                recipeNames);

        ListView listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent recipeDetailsIntent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
            recipeDetailsIntent.putExtra("id", recipeRepository.getAllRecipes().get(i).getId());
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
            case R.id.create_recipe_entry:
                Intent createRecipeIntent = new Intent(this, CreateRecipeActivity.class);
                startActivity(createRecipeIntent);
            case R.id.refresh_entry:
                new Thread(runnable).start();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}