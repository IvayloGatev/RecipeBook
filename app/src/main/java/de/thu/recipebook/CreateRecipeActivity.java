package de.thu.recipebook;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class CreateRecipeActivity extends AppCompatActivity {
    private RecipeDatabase recipeDatabase;
    private AddRecipeRunnable runnable;

    private Recipe recipe;
    private Bitmap image;

    private ActivityResultLauncher<String> chooseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        recipeDatabase = RecipeDatabase.getInstance();
        runnable = new AddRecipeRunnable(recipeDatabase, this);

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

        chooseImage = registerForActivityResult(new
                ActivityResultContracts.GetContent(), uri -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                image = bitmap;

                ImageView imageView = findViewById(R.id.image_view_upload);
                float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                imageView.getLayoutParams().height = (int) (270 * scale + 0.5f);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void selectImage(View view) {
        chooseImage.launch("image/*");
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
            String creatorId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            recipe = new Recipe(name.getText().toString(), country.getSelectedItem().toString(),
                    ingredients.getText().toString(), instructions.getText().toString(), creatorId);
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