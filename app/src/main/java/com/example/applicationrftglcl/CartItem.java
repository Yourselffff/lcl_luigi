package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant un item du panier retourné par l'API
 * Correspond à un Rental avec status = 2
 */
public class CartItem {
    @SerializedName("rentalId")
    private int rentalId;

    @SerializedName("rentalDate")
    private String rentalDate;

    @SerializedName("inventory")
    private Inventory inventory;

    @SerializedName("inventoryId")
    private int inventoryId;

    @SerializedName("customerId")
    private int customerId;

    @SerializedName("statusId")
    private int statusId;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    // Classe interne pour Inventory
    public static class Inventory {
        @SerializedName("inventoryId")
        private int inventoryId;

        @SerializedName("film")
        private Film film;

        @SerializedName("filmId")
        private int filmId;

        public int getInventoryId() {
            return inventoryId;
        }

        public Film getFilm() {
            return film;
        }

        public int getFilmId() {
            return filmId;
        }
    }

    // Constructeur vide pour Gson
    public CartItem() {
    }

    // Getters
    public int getRentalId() {
        return rentalId;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    // Méthode helper pour obtenir le Film directement
    public Film getFilm() {
        if (inventory != null) {
            return inventory.getFilm();
        }
        return null;
    }

    // Méthode helper pour obtenir le titre du film
    public String getFilmTitle() {
        Film film = getFilm();
        if (film != null) {
            return film.getTitle();
        }
        return "";
    }

    // Méthode helper pour obtenir l'ID du film
    public String getFilmId() {
        Film film = getFilm();
        if (film != null) {
            return film.getFilm_id();
        }
        return "";
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "rentalId=" + rentalId +
                ", filmTitle='" + getFilmTitle() + '\'' +
                ", statusId=" + statusId +
                '}';
    }
}
