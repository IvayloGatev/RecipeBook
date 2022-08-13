package de.thu.recipebook.activities

import android.content.Intent
import android.os.Bundle
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
import de.thu.recipebook.runnables.FetchRecipeListRunnable

class RecipeListActivity : AppCompatActivity() {
    private var recipeCollection: RecipeCollection? = null
    private var adapter: RecipeListAdapter? = null

    private var runnable: FetchRecipeListRunnable? = null

    private var listView: ListView? = null
    private var editTextSearch: EditText? = null

    private var filterName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        recipeCollection = RecipeCollection.instance
        adapter = RecipeListAdapter()
        val data: List<Recipe> = recipeCollection!!.getRecipes()
        adapter!!.setData(data)

        runnable = FetchRecipeListRunnable(this)
        Thread(runnable).start()

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
                Thread(runnable).start()
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

}