package de.thu.recipebook.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.runnables.DeleteRecipeRunnable;
import de.thu.recipebook.runnables.FetchRecipeDetailsRunnable;

public class RecipeDetailsActivity extends AppCompatActivity {
    private Recipe recipe;
    private ShareActionProvider shareActionProvider;

    private FetchRecipeDetailsRunnable fetchRecipeDetailsRunnable;
    private DeleteRecipeRunnable deleteRecipeRunnable;

    private MenuItem shareItem;
    private MenuItem editItem;
    private MenuItem deleteItem;

    private TextView nameTextView;
    private TextView countryTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        deleteRecipeRunnable = new DeleteRecipeRunnable(this);
        fetchRecipeDetailsRunnable = new FetchRecipeDetailsRunnable(this);
        new Thread(fetchRecipeDetailsRunnable).start();

        nameTextView = findViewById(R.id.text_view_name);
        countryTextView = findViewById(R.id.text_view_country);
        ingredientsTextView = findViewById(R.id.text_view_ingredients);
        instructionsTextView = findViewById(R.id.text_view_instructions);
        imageView = findViewById(R.id.image_view_picture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_details_menu, menu);
        shareItem = menu.findItem(R.id.action_share);
        editItem = menu.findItem(R.id.edit_entry);
        deleteItem = menu.findItem(R.id.delete_entry);
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_entry:
                Intent updateRecipeIntent = new Intent(this, SaveRecipeActivity.class);
                updateRecipeIntent.putExtra("recipe", recipe);
                startActivity(updateRecipeIntent);
                break;
            case R.id.delete_entry:
                new Thread(deleteRecipeRunnable).start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(fetchRecipeDetailsRunnable).start();
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

        if (recipe.isCreator()) {
            editItem.setVisible(true);
            deleteItem.setVisible(true);
        }

        setShareText();
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