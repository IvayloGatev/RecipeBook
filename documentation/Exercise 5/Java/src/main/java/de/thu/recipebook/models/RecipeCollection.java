package de.thu.recipebook.models;

import java.util.ArrayList;
import java.util.List;

public class RecipeCollection {
    private static RecipeCollection instance;

    private List<Recipe> recipes;

    private RecipeCollection() {
        recipes = new ArrayList<>();
    }

    public static RecipeCollection getInstance() {
        if (instance == null) {
            synchronized (RecipeCollection.class) {
                instance = new RecipeCollection();
            }
        }
        return instance;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes.clear();
        this.recipes.addAll(recipes);
    }
}
