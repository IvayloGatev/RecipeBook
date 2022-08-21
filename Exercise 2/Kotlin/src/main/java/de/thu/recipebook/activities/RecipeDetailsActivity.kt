package de.thu.recipebook.activities

import androidx.appcompat.app.AppCompatActivity
import de.thu.recipebook.models.RecipeCollection
import android.widget.TextView
import android.os.Bundle
import android.view.View
import de.thu.recipebook.R
import de.thu.recipebook.models.Recipe

class RecipeDetailsActivity : AppCompatActivity() {
    private var recipeCollection: RecipeCollection? = null
    private var recipe: Recipe? = null

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
        val nextRecipe = recipeCollection?.getNextRecipe(recipe)
        if (nextRecipe != null) {
            setRecipe(nextRecipe)
        }
    }

    fun nextButtonOnClick(view: View?) {
        val nextRecipe = recipeCollection!!.getNextRecipe(recipe)
        setRecipe(nextRecipe)
    }

    fun setRecipe(recipe: Recipe) {
        nameTextView!!.text = recipe.name
        countryTextView!!.text = recipe.country
        ingredientsTextView!!.text = recipe.ingredients
        instructionsTextView!!.text = recipe.instructions

        this.recipe = recipe
    }
}