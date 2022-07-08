package de.thu.recipebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

public class RecipeDetailsActivity extends AppCompatActivity {
    //    Ex. 1
//    private RecipeDatabase recipeDatabase;
    private Recipe recipe;
    private FavoritesDbHelper favoritesDbHelper;
    private ShareActionProvider shareActionProvider;
    private FetchRecipeDetailsRunnable runnable;
    private TextToSpeech textToSpeech;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

//        Exercise 5
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        favoritesDbHelper = new FavoritesDbHelper(this);
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });

        runnable = new FetchRecipeDetailsRunnable(this);
        new Thread(runnable).start();

//        Ex. 1
//        recipeDatabase = new RecipeDatabase();
//        currentRecipe = recipeDatabase.getNextRecipe(currentRecipe);

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
        TextView countryLabelTextView = findViewById(R.id.text_view_country_label);
        countryLabelTextView.setVisibility(TextView.VISIBLE);

        TextView ingredientsTextView = findViewById(R.id.text_view_ingredients);
        ingredientsTextView.setText(recipe.getIngredients());
        TextView ingredientsLabelTextView = findViewById(R.id.text_view_ingredients_label);
        ingredientsLabelTextView.setVisibility(TextView.VISIBLE);

        TextView instructionsTextView = findViewById(R.id.text_view_instructions);
        instructionsTextView.setText(recipe.getInstructions());
        TextView instructionsLabelTextView = findViewById(R.id.text_view_instructions_label);
        instructionsLabelTextView.setVisibility(TextView.VISIBLE);

        Button listenButton = findViewById(R.id.button_listen);
        listenButton.setVisibility(TextView.VISIBLE);

        ImageView imageView = findViewById(R.id.image_view_picture);
        if (recipe.getImage() != null) {
            imageView.setImageBitmap(recipe.getImage());
        } else {
            imageView.setImageResource(R.drawable.dish_picture_placeholder);
        }

        this.recipe = recipe;
        setFavoriteMenuItemText();
        setShareText();
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
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_entry:
                SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
                if (item.getTitle().equals("Add to favorites")) {
                    ContentValues values = new ContentValues();
                    values.put(BaseColumns._ID, recipe.getId());
                    db.insert(FavoritesDbHelper.FAVORITES_TABLE, null, values);
                    item.setTitle("Remove from favorites");
                } else {
                    db.delete(FavoritesDbHelper.FAVORITES_TABLE, BaseColumns._ID + "=?", new String[]{recipe.getId()});
                    item.setTitle("Add to favorites");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setShareText() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (recipe != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, recipe.toString());
        }
        shareActionProvider.setShareIntent(shareIntent);
    }

    private void setFavoriteMenuItemText() {
        SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();
        Cursor c = db.query(FavoritesDbHelper.FAVORITES_TABLE, new String[]{BaseColumns._ID},
                BaseColumns._ID + " = ?", new String[]{recipe.getId()}, null, null, null);
        if (c.getCount() > 0) {
            MenuItem favoriteItem = menu.findItem(R.id.favorite_entry);
            favoriteItem.setTitle("Remove from favorites");
        }

    }

    public void listen(View view) {
        Button button = (Button) view;
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            button.setText("Play");
        }else {
            textToSpeech.speak(recipe.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            button.setText("Stop");
        }
    }
}