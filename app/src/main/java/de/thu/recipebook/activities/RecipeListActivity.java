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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

import de.thu.recipebook.FavoritesDbHelper;
import de.thu.recipebook.LocationIntentService;
import de.thu.recipebook.R;
import de.thu.recipebook.Recipe;
import de.thu.recipebook.RecipeDatabase;
import de.thu.recipebook.RecipeListAdapter;
import de.thu.recipebook.runnables.FetchRecipeListRunnable;

public class RecipeListActivity extends AppCompatActivity {
    public static String SHOW_ALL = "Show All";
    public static String SHOW_LOCAL_ONLY = "Show Local Only";

    private RecipeDatabase database;
    private FavoritesDbHelper favoritesDbHelper;
    private RecipeListAdapter adapter;

    private FetchRecipeListRunnable runnable;
    private ResultReceiver resultReceiver;

    private ListView listView;

    private String filterCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

//        Exercise 5
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        database = RecipeDatabase.getInstance();
        favoritesDbHelper = new FavoritesDbHelper(this);
        adapter = new RecipeListAdapter(favoritesDbHelper);

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

        //Code for exercise 2
//        List<String> recipeNames = new ArrayList<>();
//        for (Recipe recipe : recipes) {
//            recipeNames.add(recipe.getName());
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                R.layout.list_view_item_recipe,
//                R.id.text_view_recipe_list,
//                recipeNames);

        listView = findViewById(R.id.list_view_recipe);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent recipeDetailsIntent = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
            recipeDetailsIntent.putExtra("id", database.getRecipes().get(i).getId());
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
                Intent addRecipeIntent = new Intent(this, AddRecipeActivity.class);
                startActivity(addRecipeIntent);
                break;
            case R.id.filter_country_entry:
                if (filterCountry == null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                        ActivityCompat.requestPermissions(this, permissions, 1);
                    }
                    Intent intent = new Intent(this, LocationIntentService.class);
                    intent.putExtra("receiver", resultReceiver);
                    startService(intent);
                    item.setTitle(SHOW_ALL);

                } else {
                    filterCountry = null;
                    item.setTitle(SHOW_LOCAL_ONLY);
                    new Thread(runnable).start();
                }
                break;
            case R.id.refresh_entry:
                new Thread(runnable).start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList() {
        List<Recipe> data = database.getRecipes();
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    public String getFilterCountry() {
        return filterCountry;
    }
}