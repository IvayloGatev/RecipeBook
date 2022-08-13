package de.thu.recipebook.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import de.thu.recipebook.R
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.runnables.DeleteRecipeRunnable
import de.thu.recipebook.runnables.FetchRecipeDetailsRunnable

class RecipeDetailsActivity : AppCompatActivity() {
    private var recipe: Recipe? = null
    private var shareActionProvider: ShareActionProvider? = null
	
    private var fetchRecipeDetailsRunnable: FetchRecipeDetailsRunnable? = null
    private var deleteRecipeRunnable: DeleteRecipeRunnable? = null

    private var shareItem: MenuItem? = null
    private var editItem: MenuItem? = null
    private var deleteItem: MenuItem? = null

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
    }

    fun countryTextViewOnClick(view: View) {
        val country = (view as TextView).text.toString()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0`?q=$country"))
        startActivity(intent)
    }

    private fun setShareText() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareActionProvider!!.setShareIntent(shareIntent)
        shareIntent.putExtra(Intent.EXTRA_TEXT, recipe.toString())
    }

}