package de.thu.recipebook.activities

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.thu.recipebook.R
import de.thu.recipebook.models.CountryCollection.countries
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.models.RecipeCollection
import okhttp3.*
import okhttp3.Credentials.basic
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SaveRecipeActivity : AppCompatActivity() {
    private var adapter: ArrayAdapter<String>? = null
    private var recipeCollection: RecipeCollection? = null
    private var recipe: Recipe? = null

    private var nameEditText: EditText? = null
    private var countrySpinner: Spinner? = null
    private var ingredientsEditText: EditText? = null
    private var instructionsEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_recipe)

        recipeCollection = RecipeCollection.instance
        adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            countries
        )
        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        countrySpinner = findViewById(R.id.spinner_country)
        countrySpinner?.adapter = adapter

        nameEditText = findViewById(R.id.edit_text_name)
        countrySpinner = findViewById(R.id.spinner_country)
        ingredientsEditText = findViewById(R.id.edit_text_ingredients)
        instructionsEditText = findViewById(R.id.edit_text_instructions)
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
            recipe = Recipe(
                null,
                nameEditText!!.text.toString(), countrySpinner!!.selectedItem.toString(),
                ingredientsEditText!!.text.toString(), instructionsEditText!!.text.toString()
            )
            saveRecipe()
            Log.i("SUCCESS", "Recipe " + recipe!!.name + " was successfully created.")
        }
    }

    private fun validate(editText: EditText?): Boolean {
        if (editText!!.text.toString().isEmpty()) {
            editText.error = "This field can't be empty!"
            return false
        }
        return true
    }

    private fun saveRecipe() {
        try {
            val response = executeQuery(recipe!!)
            if (response.isSuccessful) {
                val body = response.body!!.string()
                if (body.isNotEmpty()) {
                    val id = JSONObject(body).getString("id")
                    recipeCollection!!.getRecipes().add(Recipe(id, recipe!!.name, recipe!!.country))
                }
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun executeQuery(recipe: Recipe): Response {
        val url = "http://10.0.2.2:3000/api/recipes"
        val credentials = basic(
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            ), ""
        )

        val client: OkHttpClient =
            OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS).build()
        val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", recipe.name)
            .addFormDataPart("country", recipe.country)
            .addFormDataPart("ingredients", recipe.ingredients!!)
            .addFormDataPart("instructions", recipe.instructions!!)

        val body: RequestBody = builder.build()
        val request: Request =
            Request.Builder().url(url).header("Authorization", credentials).post(body).build()

        return client.newCall(request).execute()
    }
}