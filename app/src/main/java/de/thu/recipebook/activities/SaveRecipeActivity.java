package de.thu.recipebook.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;

import de.thu.recipebook.models.CountryCollection;
import de.thu.recipebook.runnables.SaveRecipeRunnable;
import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;

public class SaveRecipeActivity extends AppCompatActivity {
    private SaveRecipeRunnable runnable;
    private ActivityResultLauncher<String> chooseImage;
    private ArrayAdapter<String> adapter;

    private Recipe recipe;
    private byte[] image;

    private EditText nameEditText;
    private Spinner countrySpinner;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_recipe);

        runnable = new SaveRecipeRunnable(this);

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, CountryCollection.getCountries());
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
                setImage(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.WEBP, 0, stream);
                byte[] byteArray = stream.toByteArray();
                image = byteArray;
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        if (getIntent().getStringExtra("id") != null) {
            if (getIntent().getByteArrayExtra("image") != null) {
                byte[] byteArray = getIntent().getByteArrayExtra("image");
                image = byteArray;
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                setImage(bitmap);
            }

            nameEditText.setText(getIntent().getStringExtra("name"));
            countrySpinner.setSelection(CountryCollection.getCountries().indexOf(getIntent().getStringExtra("country")));
            ingredientsEditText.setText(getIntent().getStringExtra("ingredients"));
            instructionsEditText.setText(getIntent().getStringExtra("instructions"));
        }
    }

    public void selectImage(View view) {
        chooseImage.launch("image/*");
    }

    public void saveButtonOnClick(View view) {
        String id = null;
        if (getIntent().getStringExtra("id") != null) {
            id = getIntent().getStringExtra("id");
        }

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
            recipe = new Recipe(id, nameEditText.getText().toString(), countrySpinner.getSelectedItem().toString(),
                    ingredientsEditText.getText().toString(), instructionsEditText.getText().toString(), true);

            if (image != null) {
                recipe.setImage(image);
            }

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

    private void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        imageView.getLayoutParams().height = (int) (270 * scale + 0.5f);
    }

    public Recipe getRecipe() {
        return recipe;
    }
}