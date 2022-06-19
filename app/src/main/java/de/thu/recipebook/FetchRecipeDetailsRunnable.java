package de.thu.recipebook;

public class FetchRecipeDetailsRunnable implements Runnable {
    private RecipeRepository repository;
    private RecipeDetailsActivity activity;

    public FetchRecipeDetailsRunnable(RecipeRepository repository, RecipeDetailsActivity activity) {
        this.repository = repository;
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (FetchRecipeDetailsRunnable.this) {
            if (activity != null) {
                String id = activity.getId();
                Recipe recipe = repository.fetchRecipe(Integer.parseInt(id));
                activity.runOnUiThread(() -> {
                    activity.setRecipe(recipe);
                    activity.setShareText(recipe.toString());
                });
            }
        }
    }
}
