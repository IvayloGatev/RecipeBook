package de.thu.recipebook.models

class Recipe(
    val name: String,
    val country: String,
    val ingredients: String,
    val instructions: String
) {

    override fun toString(): String {
        return "$name\nCountry of origin:\n$country\nIngredients:\n$ingredients\nInstructions:\n$instructions"
    }
}