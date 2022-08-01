package de.thu.recipebook.activities

import android.os.Bundle
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
    }
}