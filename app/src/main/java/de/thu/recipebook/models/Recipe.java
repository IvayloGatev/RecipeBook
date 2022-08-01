package de.thu.recipebook.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Recipe implements Serializable {
    private String id;
    private String name;
    private String country;
    private String ingredients;
    private String instructions;
    private boolean isCreator;
    private byte[] image;

    public Recipe() {
        this(null, null, null, null, null, false);
    }

    public Recipe(String id, String name, String country) {
        this(id, name, country, null, null, false);
    }

    public Recipe(String name, String country, String ingredients, String instructions, boolean isOwner) {
        this(null, name, country, ingredients, instructions, isOwner);
    }

    public Recipe(String id, String name, String country, String ingredients, String instructions, boolean isOwner) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.isCreator = isOwner;
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

    public boolean isCreator() {
        return isCreator;
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
