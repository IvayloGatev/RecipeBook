package de.thu.recipebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class RecipeDetailsActivity extends AppCompatActivity {
    private RecipeRepository recipeRepository;

    //    Ex. 1
//    private RecipeRepository recipeRepository;
    private Recipe recipe;

    private ShareActionProvider shareActionProvider;
    private FetchRecipeDetailsRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

//        Exercise 5
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = findViewById(R.id.toolbar_recipe_details);
        setSupportActionBar(toolbar);

        recipeRepository = RecipeRepository.getInstance();
        runnable = new FetchRecipeDetailsRunnable(recipeRepository, this);
        new Thread(runnable).start();

//        Ex. 1
//        recipeRepository = new RecipeRepository();
//        currentRecipe = recipeRepository.getNextRecipe(currentRecipe);

    }

//    Ex. 1
//    public void nextButtonOnClick(View view) {
//        currentRecipe = recipeRepository.getNextRecipe(currentRecipe);
//        setRecipe();
//    }

    public void setRecipe(Recipe recipe) {
        TextView nameTextView = findViewById(R.id.text_view_name);
        nameTextView.setText(recipe.getName());

        TextView countryTextView = findViewById(R.id.text_view_country);
        countryTextView.setText(recipe.getCountry());

        TextView ingredientsTextView = findViewById(R.id.text_view_ingredients);
        ingredientsTextView.setText(recipe.getIngredients());

        TextView instructionsTextView = findViewById(R.id.text_view_instructions);
        instructionsTextView.setText(recipe.getInstructions());

        this.recipe = recipe;
    }

    public void countryTextViewOnClick(View view) {
        String country = ((TextView) view).getText().toString();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + country));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_details_menu, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        setShareText("");

        return true;
    }

    public void setShareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareActionProvider.setShareIntent(shareIntent);
    }

    public String getId() {
        return getIntent().getStringExtra("id");
    }
}