package de.thu.recipebook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;

public class RecipeDetailsActivity extends AppCompatActivity {
    private RecipeCollection recipeCollection;
    private Recipe recipe;
    private ShareActionProvider shareActionProvider;

    private MenuItem shareItem;

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
        Integer id = getIntent().getIntExtra("id", 0);
        Recipe recipe = recipeCollection.getRecipes().get(id);
        setRecipe(recipe);
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
}