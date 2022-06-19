package de.thu.recipebook;

import android.app.Activity;
import android.widget.Toast;

public class FetchRecipeListRunnable implements Runnable {
    private RecipeRepository repository;
    private Activity activity;
    private RecipeListAdapter adapter;

    FetchRecipeListRunnable(RecipeRepository repository, Activity activity, RecipeListAdapter adapter) {
        this.repository = repository;
        this.activity = activity;
        this.adapter = adapter;
    }


    @Override
    public void run() {
        repository.updateRecipeList();
        synchronized (FetchRecipeListRunnable.this) {
            if (activity != null) {
                adapter.setData(repository.getAllRecipes());
                activity.runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(activity, "Recipe List Updated!", Toast.LENGTH_SHORT);
                    toast.show();
                });
            }
        }
    }
}