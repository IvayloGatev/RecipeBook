package de.thu.recipebook.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

import de.thu.recipebook.R;
import de.thu.recipebook.databases.FavoritesDbHelper;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;
import de.thu.recipebook.models.RecipeListAdapter;
import de.thu.recipebook.runnables.FetchRecipeListRunnable;
import de.thu.recipebook.services.LocationService;

public class RecipeListActivity extends AppCompatActivity {
    public static String SHOW_ALL = "Show All";
    public static String SHOW_LOCAL_ONLY = "Show Local Only";

    private RecipeCollection recipeCollection;
    private RecipeListAdapter adapter;
    private FavoritesDbHelper favoritesDbHelper;

    private FetchRecipeListRunnable runnable;
    private ResultReceiver resultReceiver;

    private ListView listView;
    private EditText editTextSearch;

    private String filterName;
    private String filterCountry;

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

        Activity activity = this;
        resultReceiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 1) {
                    String country = resultData.getString("country");
                    filterCountry = country;
                    new Thread(runnable).start();
                } else {
                    Toast toast = Toast.makeText(activity, "Error trying to get last GPS location", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent recipeDetailsIntent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
            recipeDetailsIntent.putExtra("id", recipeCollection.getRecipes().get(i).getId());
            startActivity(recipeDetailsIntent);
        });
        editTextSearch = findViewById(R.id.edit_text_search);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
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
                break;
            case R.id.filter_country_entry:
                if (filterCountry == null) {
                    Intent intent = new Intent(this, LocationService.class);
                    intent.putExtra("receiver", resultReceiver);
                    startService(intent);
                    item.setTitle(SHOW_ALL);
                } else {
                    filterCountry = null;
                    item.setTitle(SHOW_LOCAL_ONLY);
                    new Thread(runnable).start();
                }
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

    public String getFilterCountry() {
        return filterCountry;
    }
}