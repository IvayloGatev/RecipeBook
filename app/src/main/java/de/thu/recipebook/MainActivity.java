package de.thu.recipebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private RecipeRepository repository;
    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new RecipeRepository();
        currentRecipe = repository.getNextRecipe(currentRecipe);
        setRecipe();
    }

    public void cycleButtonOnClick(View view) {
        currentRecipe = repository.getNextRecipe(currentRecipe);
        setRecipe();
    }

    private void setRecipe() {
        TextView nameTextView = (TextView) findViewById(R.id.name);
        nameTextView.setText(currentRecipe.getName());

        TextView countryTextView = (TextView) findViewById(R.id.country);
        countryTextView.setText(currentRecipe.getCountry());

        TextView ingredientsTextView = (TextView) findViewById(R.id.ingredients);
        ingredientsTextView.setText(currentRecipe.getIngredients());

        TextView instructionsTextView = (TextView) findViewById(R.id.instructions);
        instructionsTextView.setText(currentRecipe.getInstructions());

    }
}