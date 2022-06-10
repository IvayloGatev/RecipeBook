package de.thu.recipebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RecipeDetailsActivity extends AppCompatActivity {
    private RecipeRepository recipeRepository;
    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeRepository = new RecipeRepository();
        currentRecipe = recipeRepository.getNextRecipe(currentRecipe);
        setRecipe();
    }

    public void nextButtonOnClick(View view) {
        currentRecipe = recipeRepository.getNextRecipe(currentRecipe);
        setRecipe();
    }

    private void setRecipe() {
        TextView nameTextView = findViewById(R.id.text_view_name);
        nameTextView.setText(currentRecipe.getName());

        TextView countryTextView = findViewById(R.id.text_view_country);
        countryTextView.setText(currentRecipe.getCountry());

        TextView ingredientsTextView = findViewById(R.id.text_view_ingredients);
        ingredientsTextView.setText(currentRecipe.getIngredients());

        TextView instructionsTextView = findViewById(R.id.text_view_instructions);
        instructionsTextView.setText(currentRecipe.getInstructions());
    }
}