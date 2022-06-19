package de.thu.recipebook;

import android.widget.Toast;

public class AddRecipeRunnable implements Runnable {
    private RecipeRepository repository;
    private CreateRecipeActivity activity;

    AddRecipeRunnable(RecipeRepository repository, CreateRecipeActivity activity) {
        this.repository = repository;
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (AddRecipeRunnable.this) {
            if (activity != null) {
                Recipe recipe = activity.getRecipe();
                repository.addRecipe(recipe);
                activity.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(activity, "Recipe created successfully!", Toast.LENGTH_SHORT);
                    toast.show();
                });
            }
        }
    }
}
