package de.thu.recipebook.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import org.json.JSONObject;

import java.io.IOException;

import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeDetailsActivity extends AppCompatActivity {
    private Recipe recipe;
    private ShareActionProvider shareActionProvider;

    private MenuItem shareItem;

    private TextView nameTextView;
    private TextView countryTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        nameTextView = findViewById(R.id.text_view_name);
        countryTextView = findViewById(R.id.text_view_country);
        ingredientsTextView = findViewById(R.id.text_view_ingredients);
        instructionsTextView = findViewById(R.id.text_view_instructions);
        imageView = findViewById(R.id.image_view_picture);

        fetchRecipeDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_details_menu, menu);
        shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        setShareText();
        return true;
    }

    public void setRecipe(Recipe recipe) {
        nameTextView.setText(recipe.getName());
        countryTextView.setText(recipe.getCountry());
        ingredientsTextView.setText(recipe.getIngredients());
        instructionsTextView.setText(recipe.getInstructions());

        if (recipe.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(recipe.getImage(), 0, recipe.getImage().length);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.dish_picture_placeholder);
        }

        this.recipe = recipe;
    }

    public void countryTextViewOnClick(View view) {
        String country = ((TextView) view).getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + country));
        startActivity(intent);
    }

    private void setShareText() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareActionProvider.setShareIntent(shareIntent);
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipe.toString());
    }

    private void fetchRecipeDetails() {
        try {
            Response response = executeQuery();
            if (response.isSuccessful()) {
                String body = response.body().string();
                Recipe recipe = convertResponseBodyToRecipe(body);
                setRecipe(recipe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Response executeQuery() throws IOException {
        String id = getIntent().getStringExtra("id");
        String url = "http://10.0.2.2:3000/api/recipes/" + id;
        String credentials = Credentials.basic(Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID), "");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).header("Authorization", credentials).build();

        Response response = client.newCall(request).execute();
        return response;
    }


    private Recipe convertResponseBodyToRecipe(String body) throws Exception {
        JSONObject jsonObject = new JSONObject(body);
        Recipe recipe = new Recipe(jsonObject.getString("id"), jsonObject.getString("name"),
                jsonObject.getString("country"), jsonObject.getString("ingredients"),
                jsonObject.getString("instructions"));

        if (jsonObject.has("image")) {
            String imageBase64 = jsonObject.getString("image");
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            recipe.setImage(decodedString);
        }
        return recipe;
    }

}