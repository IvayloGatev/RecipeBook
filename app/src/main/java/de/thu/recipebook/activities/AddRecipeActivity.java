package de.thu.recipebook.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import de.thu.recipebook.runnables.AddRecipeRunnable;
import de.thu.recipebook.R;
import de.thu.recipebook.Recipe;
import de.thu.recipebook.RecipeDatabase;

public class AddRecipeActivity extends AppCompatActivity {
    private RecipeDatabase recipeDatabase;
    private AddRecipeRunnable runnable;
    private ActivityResultLauncher<String> chooseImage;
    private ArrayAdapter<String> adapter;

    private Recipe recipe;
    private Bitmap image;

    private EditText nameEditText;
    private Spinner countrySpinner;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        recipeDatabase = RecipeDatabase.getInstance();
        runnable = new AddRecipeRunnable(this);

        String[] countries = {"Albania", "Andorra", "Austria", "Belarus", "Belgium", "Bosnia and Herzegovina",
                "Bulgaria", "Croatia", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Germany",
                "Greece", "Hungary", "Iceland", "Ireland", "Italy", "Latvia", "Liechtenstein", "Lithuania",
                "Luxembourg", "Malta", "Moldova", "Monaco", "Montenegro", "Netherlands", "North Macedonia",
                "Norway", "Poland", "Portugal", "Romania", "Russia", "San Marino", "Serbia", "Slovakia", "Slovenia",
                "Spain", "Sweden", "Switzerland", "Ukraine", "United Kingdom"};

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner = findViewById(R.id.spinner_country);
        countrySpinner.setAdapter(adapter);

        nameEditText = findViewById(R.id.edit_text_name);
        countrySpinner = findViewById(R.id.spinner_country);
        ingredientsEditText = findViewById(R.id.edit_text_ingredients);
        instructionsEditText = findViewById(R.id.edit_text_instructions);

        imageView = findViewById(R.id.image_view_upload);
        imageView.setOnClickListener(view -> {
            if (image != null) {
                image = null;
                imageView.setImageBitmap(null);
                imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });

        chooseImage = registerForActivityResult(new
                ActivityResultContracts.GetContent(), uri -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                image = bitmap;
                imageView.setImageBitmap(bitmap);

                float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                imageView.getLayoutParams().height = (int) (270 * scale + 0.5f);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void selectImage(View view) {
        chooseImage.launch("image/*");
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
                    ingredientsEditText.getText().toString(), instructionsEditText.getText().toString(), true);
            recipe.setImage(image);

            new Thread(runnable).start();

//            Ex. 2
//            name.getText().clear();
//            country.setSelection(0);
//            ingredients.getText().clear();
//            instructions.getText().clear();
        }
    }

    private boolean validate(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("This field can't be empty!");
            return false;
        }
        return true;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}