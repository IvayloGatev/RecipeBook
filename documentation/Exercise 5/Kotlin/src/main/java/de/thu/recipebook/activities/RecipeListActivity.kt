package de.thu.recipebook.activities

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import de.thu.recipebook.R
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.models.RecipeCollection
import de.thu.recipebook.models.RecipeListAdapter
import okhttp3.Credentials.basic
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class RecipeListActivity : AppCompatActivity() {
    private var recipeCollection: RecipeCollection? = null
    private var adapter: RecipeListAdapter? = null

    private var listView: ListView? = null
    private var editTextSearch: EditText? = null

    private var filterName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        recipeCollection = RecipeCollection.instance
        adapter = RecipeListAdapter()
        fetchRecipesFromApi()
        val data: List<Recipe> = recipeCollection!!.getRecipes()
        adapter!!.setData(data)

        listView = findViewById(R.id.list_view_recipe)
        listView?.adapter = adapter
        listView?.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                val recipeDetailsIntent =
                    Intent(this@RecipeListActivity, RecipeDetailsActivity::class.java)
                recipeDetailsIntent.putExtra("id", recipeCollection!!.getRecipes()[i].id)
                startActivity(recipeDetailsIntent)
            }
        editTextSearch = findViewById(R.id.edit_text_search)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_recipe_entry -> {
                val createRecipeIntent = Intent(this, SaveRecipeActivity::class.java)
                startActivity(createRecipeIntent)
            }
            R.id.refresh_entry -> {
                fetchRecipesFromApi()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        adapter!!.notifyDataSetChanged()
        super.onResume()
    }

    private fun fetchRecipesFromApi() {
        try {
            val response = executeQuery(filterName)
            if (response.isSuccessful) {
                val body = response.body!!.string()
                val recipes = convertResponseBodyToRecipeList(body)
                recipeCollection!!.setRecipes(recipes)
                adapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun executeQuery(nameFilter: String?): Response {
        val url = "http://10.0.2.2:3000/api/recipes"
        val credentials = basic(
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            ), ""
        )

        val client = OkHttpClient()
        val httpBuilder: HttpUrl.Builder = url.toHttpUrlOrNull()!!.newBuilder()
        if (nameFilter != null && !nameFilter.isEmpty()) {
            httpBuilder.addQueryParameter("name", nameFilter)
        }

        val request: Request =
            Request.Builder().url(httpBuilder.build()).header("Authorization", credentials).build()
        return client.newCall(request).execute()
    }

    @Throws(Exception::class)
    private fun convertResponseBodyToRecipeList(body: String): MutableList<Recipe> {
        val recipes: MutableList<Recipe> = ArrayList()

        val recipesJsonArray = JSONArray(body)
        for (i in 0 until recipesJsonArray.length()) {
            val properties = recipesJsonArray.getJSONObject(i)
            Log.i("JSON", properties.toString())
            recipes.add(
                Recipe(
                    properties.getString("id"),
                    properties.getString("name"),
                    properties.getString("country")
                )
            )
        }

        return recipes
    }

    fun searchButtonOnClick(view: View?) {
        filterName = editTextSearch!!.text.toString()
        fetchRecipesFromApi()
    }
}