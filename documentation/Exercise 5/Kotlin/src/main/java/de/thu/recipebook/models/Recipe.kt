package de.thu.recipebook.models

class Recipe constructor(
    val id: String?,
    val name: String,
    val country: String,
    val ingredients: String? = null,
    val instructions: String? = null
) {
    var image: ByteArray? = null

    constructor(name: String, country: String, ingredients: String?, instructions: String?) : this(
        null,
        name,
        country,
        ingredients,
        instructions
    )

    override fun toString(): String {
        return """
               $name
               Country of origin:
               $country
               Ingredients:
               $ingredients
               Instructions:
               $instructions
               """.trimIndent()
    }
}