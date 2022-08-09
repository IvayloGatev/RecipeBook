package de.thu.recipebook.activities;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.thu.recipebook.R;
import de.thu.recipebook.models.CountryCollection;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;
import okhttp3.Credentials;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
            saveRecipe();
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

    private void saveRecipe() {
        try {
            Response response = executeQuery(recipe);
            if (response.isSuccessful()) {
                String body = response.body().string();
                if (!body.isEmpty()) {
                    String id = new JSONObject(body).getString("id");
                    recipeCollection.getRecipes().add(new Recipe(id, recipe.getName(), recipe.getCountry()));
                }
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response executeQuery(Recipe recipe) throws IOException {
        String url = "http://10.0.2.2:3000/api/recipes";
        String credentials = Credentials.basic(Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID), "");

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", recipe.getName())
                .addFormDataPart("country", recipe.getCountry())
                .addFormDataPart("ingredients", recipe.getIngredients())
                .addFormDataPart("instructions", recipe.getInstructions());

        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).header("Authorization", credentials).post(body).build();

        Response response = client.newCall(request).execute();
        return response;
    }

}