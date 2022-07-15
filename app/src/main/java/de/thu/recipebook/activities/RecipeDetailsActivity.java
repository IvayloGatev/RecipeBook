package de.thu.recipebook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import de.thu.recipebook.databases.FavoritesDbHelper;
import de.thu.recipebook.runnables.DeleteRecipeRunnable;
import de.thu.recipebook.runnables.FetchRecipeDetailsRunnable;
import de.thu.recipebook.R;
import de.thu.recipebook.models.Recipe;

public class RecipeDetailsActivity extends AppCompatActivity {
    public String ADD_TO_FAVORITES = "Add To Favorites";
    public String REMOVE_FROM_FAVORITES = "Remove From Favorites";

    //    Ex. 1
//    private RecipeDatabase recipeDatabase;
    private ShareActionProvider shareActionProvider;
    private FetchRecipeDetailsRunnable fetchRecipeDetailsRunnable;
    private DeleteRecipeRunnable deleteRecipeRunnable;
    private FavoritesDbHelper favoritesDbHelper;
    private TextToSpeech textToSpeech;

    private MenuItem shareItem;
    private MenuItem editItem;
    private MenuItem deleteItem;
    private MenuItem favoriteItem;

    private Recipe recipe;

    private TextView nameTextView;
    private TextView countryTextView;
    private TextView countryLabelTextView;
    private TextView ingredientsTextView;
    private TextView ingredientsLabelTextView;
    private TextView instructionsTextView;
    private TextView instructionsLabelTextView;
    private Button listenButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

//        Exercise 5
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        deleteRecipeRunnable = new DeleteRecipeRunnable(this);
        fetchRecipeDetailsRunnable = new FetchRecipeDetailsRunnable(this);
        new Thread(fetchRecipeDetailsRunnable).start();

        favoritesDbHelper = new FavoritesDbHelper(this);
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });
//        Ex. 1
//        recipeDatabase = new RecipeDatabase();
//        currentRecipe = recipeDatabase.getNextRecipe(currentRecipe);

        nameTextView = findViewById(R.id.text_view_name);
        countryTextView = findViewById(R.id.text_view_country);
        countryLabelTextView = findViewById(R.id.text_view_country_label);
        ingredientsTextView = findViewById(R.id.text_view_ingredients);
        ingredientsLabelTextView = findViewById(R.id.text_view_ingredients_label);
        instructionsTextView = findViewById(R.id.text_view_instructions);
        instructionsLabelTextView = findViewById(R.id.text_view_instructions_label);
        listenButton = findViewById(R.id.button_listen);
        imageView = findViewById(R.id.image_view_picture);
    }

//    Ex. 1
//    public void nextButtonOnClick(View view) {
//        currentRecipe = recipeRepository.getNextRecipe(currentRecipe);
//        setRecipe();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_details_menu, menu);
        shareItem = menu.findItem(R.id.action_share);
        editItem = menu.findItem(R.id.edit_entry);
        deleteItem = menu.findItem(R.id.delete_entry);
        favoriteItem = menu.findItem(R.id.favorite_entry);
        shareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_entry:
                SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
                if (item.getTitle().equals(ADD_TO_FAVORITES)) {
                    ContentValues values = new ContentValues();
                    values.put(BaseColumns._ID, recipe.getId());
                    db.insert(FavoritesDbHelper.FAVORITES_TABLE, null, values);
                    item.setTitle(REMOVE_FROM_FAVORITES);
                } else {
                    db.delete(FavoritesDbHelper.FAVORITES_TABLE, BaseColumns._ID + "=?", new String[]{recipe.getId()});
                    item.setTitle(ADD_TO_FAVORITES);
                }
                break;
            case R.id.edit_entry:
                Intent updateRecipeIntent = new Intent(this, SaveRecipeActivity.class);
                updateRecipeIntent.putExtra("id", recipe.getId());
                updateRecipeIntent.putExtra("name", recipe.getName());
                updateRecipeIntent.putExtra("country", recipe.getCountry());
                updateRecipeIntent.putExtra("ingredients", recipe.getIngredients());
                updateRecipeIntent.putExtra("instructions", recipe.getInstructions());
                updateRecipeIntent.putExtra("image", recipe.getImage());

                startActivity(updateRecipeIntent);
                break;
            case R.id.delete_entry:
                new Thread(deleteRecipeRunnable).start();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRecipe(Recipe recipe) {
        nameTextView.setText(recipe.getName());
        countryTextView.setText(recipe.getCountry());
        countryLabelTextView.setVisibility(TextView.VISIBLE);
        ingredientsTextView.setText(recipe.getIngredients());
        ingredientsLabelTextView.setVisibility(TextView.VISIBLE);
        instructionsTextView.setText(recipe.getInstructions());
        instructionsLabelTextView.setVisibility(TextView.VISIBLE);
        listenButton.setVisibility(TextView.VISIBLE);

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

        setFavoriteMenuItemText();
        setShareText();
    }

    public void countryTextViewOnClick(View view) {
        String country = ((TextView) view).getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=" + country));
        startActivity(intent);
    }

    public void listen(View view) {
        Button button = (Button) view;
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            button.setText("Play");
        } else {
            textToSpeech.speak(recipe.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            button.setText("Stop");
        }
    }

    private void setShareText() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareActionProvider.setShareIntent(shareIntent);
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipe.toString());
        shareItem.setVisible(true);
    }

    private void setFavoriteMenuItemText() {
        SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();
        Cursor c = db.query(FavoritesDbHelper.FAVORITES_TABLE, new String[]{BaseColumns._ID},
                BaseColumns._ID + " = ?", new String[]{recipe.getId()}, null, null, null);
        favoriteItem.setVisible(true);
        if (c.getCount() > 0) {
            favoriteItem.setTitle(REMOVE_FROM_FAVORITES);
        }

    }
}