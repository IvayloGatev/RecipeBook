package de.thu.recipebook.models

import java.io.Serializable

class Recipe constructor(
    val id: String?,
    val name: String,
    val country: String,
    val ingredients: String? = null,
    val instructions: String? = null,
    val isCreator: Boolean?
) : Serializable {
    var image: ByteArray? = null

    constructor(
        name: String,
        country: String,
        ingredients: String?,
        instructions: String?,
        isCreator: Boolean?
    ) : this(
        null,
        name,
        country,
        ingredients,
        instructions,
        isCreator
    )

    override fun toString(): String {
        return "$name\nCountry of origin:\n$country\nIngredients:\n$ingredients\nInstructions:\n$instructions"
    }
}