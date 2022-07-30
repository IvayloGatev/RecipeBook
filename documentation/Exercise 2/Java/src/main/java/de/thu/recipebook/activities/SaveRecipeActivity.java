package de.thu.recipebook.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import de.thu.recipebook.R;
import de.thu.recipebook.models.CountryCollection;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;

public class SaveRecipeActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private RecipeCollection recipeCollection;
    private Recipe recipe;

    private EditText nameEditText;
    private Spinner countrySpinner;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_recipe);

        recipeCollection = RecipeCollection.getInstance();
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CountryCollection.getCountries());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        countrySpinner = findViewById(R.id.spinner_country);
        countrySpinner.setAdapter(adapter);

        nameEditText = findViewById(R.id.edit_text_name);
        countrySpinner = findViewById(R.id.spinner_country);
        ingredientsEditText = findViewById(R.id.edit_text_ingredients);
        instructionsEditText = findViewById(R.id.edit_text_instructions);
    }


    public void saveButtonOnClick(View view) {
        boolean isValid = true;
        if (!validate(nameEditText)) {
            isValid = false;
        }
        if (!validate(ingredientsEditText)) {
            isValid = false;
        }
        if (!validate(instructionsEditText)) {
            isValid = false;
        }

        if (isValid) {
            recipe = new Recipe(nameEditText.getText().toString(), countrySpinner.getSelectedItem().toString(),
                    ingredientsEditText.getText().toString(), instructionsEditText.getText().toString());
            recipeCollection.getRecipes().add(recipe);
            Log.i("SUCCESS", "Recipe " + recipe.getName() + " was successfully created.");
        }
    }

    private boolean validate(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("This field can't be empty!");
            return false;
        }
        return true;
    }
}