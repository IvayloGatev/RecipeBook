package de.thu.recipebook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;

public class RecipeDetailsActivity extends AppCompatActivity {
    private RecipeCollection recipeCollection;
    private Recipe recipe;

    private TextView nameTextView;
    private TextView countryTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        nameTextView = findViewById(R.id.text_view_name);
        countryTextView = findViewById(R.id.text_view_country);
        ingredientsTextView = findViewById(R.id.text_view_ingredients);
        instructionsTextView = findViewById(R.id.text_view_instructions);

        recipeCollection = RecipeCollection.getInstance();
        Recipe nextRecipe = recipeCollection.getNextRecipe(recipe);
        setRecipe(nextRecipe);
    }

    public void nextButtonOnClick(View view) {
        Recipe nextRecipe = recipeCollection.getNextRecipe(recipe);
        setRecipe(nextRecipe);
    }


    public void setRecipe(Recipe recipe) {
        nameTextView.setText(recipe.getName());
        countryTextView.setText(recipe.getCountry());
        ingredientsTextView.setText(recipe.getIngredients());
        instructionsTextView.setText(recipe.getInstructions());

        this.recipe = recipe;
    }
}