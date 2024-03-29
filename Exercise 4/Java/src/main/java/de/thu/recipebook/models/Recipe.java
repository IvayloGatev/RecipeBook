package de.thu.recipebook.models;

public class Recipe {
    private String name;
    private String country;
    private String ingredients;
    private String instructions;

    public Recipe(String name, String country, String ingredients, String instructions) {
        this.name = name;
        this.country = country;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        return name + "\n" + "Country of origin:\n" + country + "\nIngredients:\n" + ingredients
                + "\nInstructions:\n" + instructions;
    }
}
