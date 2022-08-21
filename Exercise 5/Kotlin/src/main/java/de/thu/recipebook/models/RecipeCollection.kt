package de.thu.recipebook.models

class RecipeCollection private constructor() {
    private val recipes: MutableList<Recipe>

    companion object {
        var instance: RecipeCollection? = null
            get() {
                if (field == null) {
                    synchronized(RecipeCollection::class.java) { field = RecipeCollection() }
                }
                return field
            }
            private set
    }

    init {
        recipes = ArrayList()
    }

    fun getRecipes(): MutableList<Recipe> {
        return recipes
    }

    fun setRecipes(recipes: MutableList<Recipe>) {
        this.recipes.clear()
        this.recipes.addAll(recipes)
    }
}