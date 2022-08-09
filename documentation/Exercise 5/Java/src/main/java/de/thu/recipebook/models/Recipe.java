package de.thu.recipebook.models;

public class Recipe {
    private String id;
    private String name;
    private String country;
    private String ingredients;
    private String instructions;
    private byte[] image;

    public Recipe(String id, String name, String country) {
        this(id, name, country, null, null);
    }

    public Recipe(String name, String country, String ingredients, String instructions) {
        this(null, name, country, ingredients, instructions);
    }

    public Recipe(String id, String name, String country, String ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getId() {
        return id;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name + "\n" + "Country of origin:\n" + country + "\nIngredients:\n" + ingredients
                + "\nInstructions:\n" + instructions;
    }
}
