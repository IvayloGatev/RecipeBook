package de.thu.recipebook.activities

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import de.thu.recipebook.R
import de.thu.recipebook.databases.FavoritesDbHelper
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.runnables.DeleteRecipeRunnable
import de.thu.recipebook.runnables.FetchRecipeDetailsRunnable
import java.util.*

class RecipeDetailsActivity : AppCompatActivity() {
    var ADD_TO_FAVORITES = "Add To Favorites"
    var REMOVE_FROM_FAVORITES = "Remove From Favorites"

    private var recipe: Recipe? = null
    private var shareActionProvider: ShareActionProvider? = null
    private var favoritesDbHelper: FavoritesDbHelper? = null
    private var textToSpeech: TextToSpeech? = null

    private var fetchRecipeDetailsRunnable: FetchRecipeDetailsRunnable? = null
    private var deleteRecipeRunnable: DeleteRecipeRunnable? = null

    private var shareItem: MenuItem? = null
    private var editItem: MenuItem? = null
    private var deleteItem: MenuItem? = null
    private var favoriteItem: MenuItem? = null

    private var nameTextView: TextView? = null
    private var countryTextView: TextView? = null
    private var ingredientsTextView: TextView? = null
    private var instructionsTextView: TextView? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        deleteRecipeRunnable = DeleteRecipeRunnable(this)
        fetchRecipeDetailsRunnable = FetchRecipeDetailsRunnable(this)
        Thread(fetchRecipeDetailsRunnable).start()

        favoritesDbHelper = FavoritesDbHelper(this)
        textToSpeech = TextToSpeech(applicationContext) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.UK
            }
        }

        nameTextView = findViewById(R.id.text_view_name)
        countryTextView = findViewById(R.id.text_view_country)
        ingredientsTextView = findViewById(R.id.text_view_ingredients)
        instructionsTextView = findViewById(R.id.text_view_instructions)
        imageView = findViewById(R.id.image_view_picture)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.recipe_details_menu, menu)
        shareItem = menu.findItem(R.id.action_share)
        editItem = menu.findItem(R.id.edit_entry)
        deleteItem = menu.findItem(R.id.delete_entry)
        favoriteItem = menu.findItem(R.id.favorite_entry)
        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_entry -> {
                val updateRecipeIntent = Intent(this, SaveRecipeActivity::class.java)
                updateRecipeIntent.putExtra("recipe", recipe)
                startActivity(updateRecipeIntent)
            }
            R.id.delete_entry -> Thread(deleteRecipeRunnable).start()
            R.id.favorite_entry -> {
                val db = favoritesDbHelper!!.writableDatabase
                if (item.title == ADD_TO_FAVORITES) {
                    val values = ContentValues()
                    values.put(BaseColumns._ID, recipe!!.id)
                    db.insert(FavoritesDbHelper.FAVORITES_TABLE, null, values)
                    item.title = REMOVE_FROM_FAVORITES
                } else {
                    db.delete(
                        FavoritesDbHelper.FAVORITES_TABLE, BaseColumns._ID + "=?", arrayOf(
                            recipe!!.id
                        )
                    )
                    item.title = ADD_TO_FAVORITES
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        Thread(fetchRecipeDetailsRunnable).start()
    }

    fun setRecipe(recipe: Recipe) {
        nameTextView!!.text = recipe.name
        countryTextView!!.text = recipe.country
        ingredientsTextView!!.text = recipe.ingredients
        instructionsTextView!!.text = recipe.instructions

        if (recipe.image != null) {
            val bitmap = BitmapFactory.decodeByteArray(recipe.image, 0, recipe.image!!.size)
            imageView!!.setImageBitmap(bitmap)
        } else {
            imageView!!.setImageResource(R.drawable.dish_picture_placeholder)
        }

        this.recipe = recipe

        if (recipe.isCreator!!) {
            editItem!!.isVisible = true
            deleteItem!!.isVisible = true
        }

        setShareText()
        setFavoriteMenuItemText()
    }

    fun countryTextViewOnClick(view: View) {
        val country = (view as TextView).text.toString()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=$country"))
        startActivity(intent)
    }

    fun listen(view: View) {
        val button = view as Button
        if (textToSpeech!!.isSpeaking) {
            textToSpeech!!.stop()
            button.text = "Play"
        } else {
            textToSpeech!!.speak(recipe.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
            button.text = "Stop"
        }
    }

    private fun setShareText() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareActionProvider!!.setShareIntent(shareIntent)
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipe.toString())
    }

    private fun setFavoriteMenuItemText() {
        val db = favoritesDbHelper!!.readableDatabase
        val c = db.query(
            FavoritesDbHelper.FAVORITES_TABLE, arrayOf(BaseColumns._ID),
            BaseColumns._ID + " = ?", arrayOf(recipe!!.id), null, null, null
        )
        favoriteItem!!.isVisible = true
        if (c.count > 0) {
            favoriteItem!!.title = REMOVE_FROM_FAVORITES
        }
    }
}