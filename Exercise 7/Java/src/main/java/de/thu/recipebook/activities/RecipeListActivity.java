package de.thu.recipebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.thu.recipebook.R;
import de.thu.recipebook.databases.FavoritesDbHelper;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;
import de.thu.recipebook.models.RecipeListAdapter;
import de.thu.recipebook.runnables.FetchRecipeListRunnable;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeListActivity extends AppCompatActivity {
    private RecipeCollection recipeCollection;
    private RecipeListAdapter adapter;
    private FavoritesDbHelper favoritesDbHelper;

    private FetchRecipeListRunnable runnable;

    private ListView listView;
    private EditText editTextSearch;

    private String filterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        recipeCollection = RecipeCollection.getInstance();
        favoritesDbHelper = new FavoritesDbHelper(this);
        adapter = new RecipeListAdapter(favoritesDbHelper);
        List<Recipe> data = recipeCollection.getRecipes();
        adapter.setData(data);

        runnable = new FetchRecipeListRunnable(this);
        new Thread(runnable).start();

        listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent recipeDetailsIntent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
            recipeDetailsIntent.putExtra("id", recipeCollection.getRecipes().get(i).getId());
            startActivity(recipeDetailsIntent);
        });
        editTextSearch = findViewById(R.id.edit_text_search);
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
                break;
            case R.id.refresh_entry:
                new Thread(runnable).start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    public void searchButtonOnClick(View view) {
        filterName = editTextSearch.getText().toString();
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public String getFilterName() {
        return filterName;
    }
}