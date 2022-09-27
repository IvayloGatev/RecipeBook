package de.thu.recipebook.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.thu.recipebook.R
import de.thu.recipebook.models.CountryCollection.countries
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.runnables.SaveRecipeRunnable

class SaveRecipeActivity : AppCompatActivity() {
    private var adapter: ArrayAdapter<String>? = null
    private var recipe: Recipe? = null

    private var runnable: SaveRecipeRunnable? = null

    private var nameEditText: EditText? = null
    private var countrySpinner: Spinner? = null
    private var ingredientsEditText: EditText? = null
    private var instructionsEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_recipe)

        adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            countries
        )
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        runnable = SaveRecipeRunnable(this)

        countrySpinner = findViewById(R.id.spinner_country)
        countrySpinner?.adapter = adapter

        nameEditText = findViewById(R.id.edit_text_name)
        countrySpinner = findViewById(R.id.spinner_country)
        ingredientsEditText = findViewById(R.id.edit_text_ingredients)
        instructionsEditText = findViewById(R.id.edit_text_instructions)

        if (intent.getSerializableExtra("recipe") != null) {
            recipe = intent.getSerializableExtra("recipe") as Recipe?
            nameEditText!!.setText(recipe!!.name)
            countrySpinner!!.setSelection(countries.indexOf(recipe!!.country))
            ingredientsEditText!!.setText(recipe!!.ingredients)
            instructionsEditText!!.setText(recipe!!.instructions)
        }
    }

    fun saveButtonOnClick(view: View?) {
        var isValid = true
        if (!validate(nameEditText)) {
            isValid = false
        }
        if (!validate(ingredientsEditText)) {
            isValid = false
        }
        if (!validate(instructionsEditText)) {
            isValid = false
        }

        if (isValid) {
            var id: String? = null
            if (recipe != null) {
                id = recipe!!.id
            }

            recipe = Recipe(
                id, nameEditText!!.text.toString(), countrySpinner!!.selectedItem.toString(),
                ingredientsEditText!!.text.toString(), instructionsEditText!!.text.toString(), true
            )
            Thread(runnable).start()
        }
    }

    private fun validate(editText: EditText?): Boolean {
        if (editText!!.text.toString().isEmpty()) {
            editText.error = "This field can't be empty!"
            return false
        }
        return true
    }

    fun getRecipe(): Recipe? {
        return recipe
    }
}