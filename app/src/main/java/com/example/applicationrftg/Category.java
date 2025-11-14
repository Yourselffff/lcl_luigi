package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant une catégorie
 */
public class Category {
    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    // Constructeur vide
    public Category() {
    }

    // Getters et Setters
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return name;
    }
}
