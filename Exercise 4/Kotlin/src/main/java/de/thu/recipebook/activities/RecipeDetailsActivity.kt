package de.thu.recipebook.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import de.thu.recipebook.R
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.models.RecipeCollection

class RecipeDetailsActivity : AppCompatActivity() {
    private var recipeCollection: RecipeCollection? = null
    private var recipe: Recipe? = null
    private var shareActionProvider: ShareActionProvider? = null

    private var shareItem: MenuItem? = null

    private var nameTextView: TextView? = null
    private var countryTextView: TextView? = null
    private var ingredientsTextView: TextView? = null
    private var instructionsTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        nameTextView = findViewById(R.id.text_view_name)
        countryTextView = findViewById(R.id.text_view_country)
        ingredientsTextView = findViewById(R.id.text_view_ingredients)
        instructionsTextView = findViewById(R.id.text_view_instructions)

        recipeCollection = RecipeCollection.instance
        val id = intent.getIntExtra("id", 0)
        val recipe = recipeCollection!!.getRecipes()[id]
        setRecipe(recipe)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.recipe_details_menu, menu)
        shareItem = menu.findItem(R.id.action_share)
        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as ShareActionProvider
        setShareText()
        return true
    }

    fun setRecipe(recipe: Recipe) {
        nameTextView!!.text = recipe.name
        countryTextView!!.text = recipe.country
        ingredientsTextView!!.text = recipe.ingredients
        instructionsTextView!!.text = recipe.instructions

        this.recipe = recipe
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