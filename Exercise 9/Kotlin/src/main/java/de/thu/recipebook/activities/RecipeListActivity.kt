package de.thu.recipebook.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import de.thu.recipebook.R
import de.thu.recipebook.databases.FavoritesDbHelper
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.models.RecipeCollection
import de.thu.recipebook.models.RecipeListAdapter
import de.thu.recipebook.runnables.FetchRecipeListRunnable
import de.thu.recipebook.services.LocationService

class RecipeListActivity : AppCompatActivity() {
    var SHOW_ALL = "Show All"
    var SHOW_LOCAL_ONLY = "Show Local Only"

    private var recipeCollection: RecipeCollection? = null
    private var adapter: RecipeListAdapter? = null
    private var favoritesDbHelper: FavoritesDbHelper? = null

    private var runnable: FetchRecipeListRunnable? = null
    private var resultReceiver: ResultReceiver? = null

    private var listView: ListView? = null
    private var editTextSearch: EditText? = null

    private var filterName: String? = null
    private var filterCountry: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        recipeCollection = RecipeCollection.instance
        favoritesDbHelper = FavoritesDbHelper(this)
        adapter = RecipeListAdapter(favoritesDbHelper!!)
        val data: List<Recipe> = recipeCollection!!.getRecipes()
        adapter!!.setData(data)

        runnable = FetchRecipeListRunnable(this)
        Thread(runnable).start()

        val activity: Activity = this
        resultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == 1) {
                    val country = resultData.getString("country")
                    filterCountry = country
                    Thread(runnable).start()
                } else {
                    val toast = Toast.makeText(
                        activity,
                        "Error trying to get last GPS location",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                }
            }
        }

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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
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
                Thread(runnable).start()
            }
            R.id.filter_country_entry ->{
                if (filterCountry == null) {
                    val intent = Intent(this, LocationService::class.java)
                    intent.putExtra("receiver", resultReceiver)
                    startService(intent)
                    item.title = SHOW_ALL
                } else {
                    filterCountry = null
                    item.title = SHOW_LOCAL_ONLY
                    Thread(runnable).start()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        adapter!!.notifyDataSetChanged()
        super.onResume()
    }

    fun searchButtonOnClick(view: View?) {
        filterName = editTextSearch!!.text.toString()
    }

    fun notifyDataSetChanged() {
        adapter!!.notifyDataSetChanged()
    }

    fun getFilterName(): String? {
        return filterName
    }

    fun getFilterCountry(): String? {
        return filterCountry
    }


}