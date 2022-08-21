package de.thu.recipebook.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
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
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RecipeDetailsActivity : AppCompatActivity() {
    private var recipe: Recipe? = null
    private var shareActionProvider: ShareActionProvider? = null

    private var shareItem: MenuItem? = null

    private var nameTextView: TextView? = null
    private var countryTextView: TextView? = null
    private var ingredientsTextView: TextView? = null
    private var instructionsTextView: TextView? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        nameTextView = findViewById(R.id.text_view_name)
        countryTextView = findViewById(R.id.text_view_country)
        ingredientsTextView = findViewById(R.id.text_view_ingredients)
        instructionsTextView = findViewById(R.id.text_view_instructions)
        imageView = findViewById(R.id.image_view_picture)

        fetchRecipeDetails()
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

        if (recipe.image != null) {
            val bitmap = BitmapFactory.decodeByteArray(recipe.image, 0, recipe.image!!.size)
            imageView!!.setImageBitmap(bitmap)
        } else {
            imageView!!.setImageResource(R.drawable.dish_picture_placeholder)
        }

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

    private fun fetchRecipeDetails() {
        try {
            val response: Response = executeQuery()
            if (response.isSuccessful) {
                val body: String = response.body!!.string()
                val recipe = convertResponseBodyToRecipe(body)
                setRecipe(recipe)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun executeQuery(): Response {
        val id = intent.getStringExtra("id")
        val url = "http://10.0.2.2:3000/api/recipes/$id"
        val credentials: String = Credentials.basic(
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            ), ""
        )

        val client = OkHttpClient()
        val request: Request = Request.Builder().url(url).header("Authorization", credentials).build()

        return client.newCall(request).execute()
    }


    @Throws(Exception::class)
    private fun convertResponseBodyToRecipe(body: String): Recipe {
        val jsonObject = JSONObject(body)
        val recipe = Recipe(
            jsonObject.getString("id"), jsonObject.getString("name"),
            jsonObject.getString("country"), jsonObject.getString("ingredients"),
            jsonObject.getString("instructions")
        )

        if (jsonObject.has("image")) {
            val imageBase64 = jsonObject.getString("image")
            val decodedString = Base64.decode(imageBase64, Base64.DEFAULT)
            recipe.image = decodedString
        }
        return recipe
    }
}