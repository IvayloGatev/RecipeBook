package de.thu.recipebook.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import de.thu.recipebook.R
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.models.RecipeCollection
import de.thu.recipebook.models.RecipeListAdapter

class RecipeListActivity : AppCompatActivity() {
    private var recipeCollection: RecipeCollection? = null
    private var adapter: RecipeListAdapter? = null

    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        recipeCollection = RecipeCollection.instance
        val recipeNames: MutableList<String> = ArrayList()
        for (recipe in recipeCollection?.getRecipes()!!) {
            recipeNames.add(recipe.name)

        }

        adapter = RecipeListAdapter()
        val data: List<Recipe> = recipeCollection!!.getRecipes()
        adapter!!.setData(data)

        listView = findViewById(R.id.list_view_recipe)
        listView?.adapter = adapter
        listView?.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, i: Int, _: Long ->
                val recipeDetailsIntent =
                    Intent(this@RecipeListActivity, RecipeDetailsActivity::class.java)
                recipeDetailsIntent.putExtra("id", i)
                startActivity(recipeDetailsIntent)
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
        }
        return super.onOptionsItemSelected(item)
    }
}