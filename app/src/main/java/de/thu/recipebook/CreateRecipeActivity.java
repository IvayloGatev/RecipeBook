package de.thu.recipebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateRecipeActivity extends AppCompatActivity {
    private RecipeRepository recipeRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        recipeRepository = new RecipeRepository();

        String[] countries = {"Albania", "Andorra", "Austria", "Belarus", "Belgium", "Bosnia and Herzegovina",
                "Bulgaria", "Croatia", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Germany",
                "Greece", "Hungary", "Iceland", "Ireland", "Italy", "Latvia", "Liechtenstein", "Lithuania",
                "Luxembourg", "Malta", "Moldova", "Monaco", "Montenegro", "Netherlands", "North Macedonia",
                "Norway", "Poland", "Portugal", "Romania", "Russia", "San Marino", "Serbia", "Slovakia", "Slovenia",
                "Spain", "Sweden", "Switzerland", "Ukraine", "United Kingdom"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner countriesSpinner = findViewById(R.id.spinner_country);
        countriesSpinner.setAdapter(adapter);
    }

    public void saveButtonOnClick(View view) {
        EditText name = findViewById(R.id.edit_text_name);
        Spinner country = findViewById(R.id.spinner_country);
        EditText ingredients = findViewById(R.id.edit_text_ingredients);
        EditText instructions = findViewById(R.id.edit_text_instructions);

        boolean isValid = true;
        if (!validate(name)) {
            isValid = false;
        }
        if (!validate(ingredients)) {
            isValid = false;
        }
        if (!validate(instructions)) {
            isValid = false;
        }

        if (isValid) {
            Recipe recipe = new Recipe(name.getText().toString(), country.getSelectedItem().toString(),
                    ingredients.getText().toString(), instructions.getText().toString());
            recipeRepository.addRecipe(recipe);
            name.getText().clear();
            country.setSelection(0);
            ingredients.getText().clear();
            instructions.getText().clear();
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