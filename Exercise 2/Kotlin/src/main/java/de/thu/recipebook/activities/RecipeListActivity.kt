package de.thu.recipebook.activities

import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.os.Bundle
import android.widget.ListView
import de.thu.recipebook.R
import de.thu.recipebook.models.RecipeCollection
import java.util.ArrayList

class RecipeListActivity : AppCompatActivity() {
    private var recipeCollection: RecipeCollection? = null
    private var adapter: ArrayAdapter<String>? = null

    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)

        recipeCollection = RecipeCollection.instance
        val recipeNames: MutableList<String> = ArrayList()
        for (recipe in recipeCollection?.getRecipes()!!) {
            recipeNames.add(recipe.name)

        }

        adapter = ArrayAdapter(
            this,
            R.layout.list_view_item_recipe,
            R.id.text_view_recipe,
            recipeNames
        )
        listView = findViewById(R.id.list_view_recipe)
        listView?.adapter = adapter
    }
}