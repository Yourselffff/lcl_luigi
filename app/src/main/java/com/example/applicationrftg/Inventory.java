package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;

public class Inventory {
    @SerializedName("inventoryId")
    private int inventoryId;

    @SerializedName("filmId")
    private int filmId;

    @SerializedName("storeId")
    private int storeId;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    // Constructeurs
    public Inventory() {
    }

    public Inventory(int inventoryId, int filmId, int storeId, String lastUpdate) {
        this.inventoryId = inventoryId;
        this.filmId = filmId;
        this.storeId = storeId;
        this.lastUpdate = lastUpdate;
    }

    // Getters et Setters
    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId=" + inventoryId +
                ", filmId=" + filmId +
                ", storeId=" + storeId +
                ", lastUpdate='" + lastUpdate + '\'' +
                '}';
    }
}
