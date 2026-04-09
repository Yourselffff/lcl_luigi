package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle de données représentant un article du panier retourné par l'API.
 * Correspond à un enregistrement Rental avec statusId = 2 (en cours de sélection).
 */
public class CartItem {

    /** Identifiant unique de la location (rental) en base de données. */
    @SerializedName("rentalId")
    private int rentalId;

    /** Date à laquelle l'article a été ajouté au panier. */
    @SerializedName("rentalDate")
    private String rentalDate;

    /** Détails de l'exemplaire physique associé à cette location. */
    @SerializedName("inventory")
    private Inventory inventory;

    /** Identifiant de l'exemplaire (sans l'objet imbriqué). */
    @SerializedName("inventoryId")
    private int inventoryId;

    /** Identifiant du client propriétaire de cet article. */
    @SerializedName("customerId")
    private int customerId;

    /** Statut de la location (2 = panier, 3 = validé). */
    @SerializedName("statusId")
    private int statusId;

    /** Date de dernière mise à jour de l'enregistrement. */
    @SerializedName("lastUpdate")
    private String lastUpdate;

    /**
     * Classe interne représentant l'exemplaire physique imbriqué dans la réponse API.
     * Contient la référence au film ainsi que ses identifiants.
     */
    public static class Inventory {

        /** Identifiant unique de l'exemplaire. */
        @SerializedName("inventoryId")
        private int inventoryId;

        /** Objet Film complet associé à cet exemplaire. */
        @SerializedName("film")
        private Film film;

        /** Identifiant du film (sans l'objet imbriqué). */
        @SerializedName("filmId")
        private int filmId;

        /** @return l'identifiant de l'exemplaire. */
        public int getInventoryId() { return inventoryId; }

        /** @return l'objet Film associé à cet exemplaire. */
        public Film getFilm() { return film; }

        /** @return l'identifiant du film. */
        public int getFilmId() { return filmId; }
    }

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public CartItem() {
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return l'identifiant de la location. */
    public int getRentalId() { return rentalId; }

    /** @return la date d'ajout au panier. */
    public String getRentalDate() { return rentalDate; }

    /** @return l'objet Inventory imbriqué. */
    public Inventory getInventory() { return inventory; }

    /** @return l'identifiant de l'exemplaire. */
    public int getInventoryId() { return inventoryId; }

    /** @return l'identifiant du client. */
    public int getCustomerId() { return customerId; }

    /** @return le statut de la location (2 = panier, 3 = validé). */
    public int getStatusId() { return statusId; }

    /** @return la date de dernière mise à jour. */
    public String getLastUpdate() { return lastUpdate; }

    // ── Méthodes helper ───────────────────────────────────────────────────────

    /**
     * Raccourci pour accéder directement au Film sans passer par Inventory.
     *
     * @return l'objet Film ou null si l'inventaire est absent.
     */
    public Film getFilm() {
        if (inventory != null) {
            return inventory.getFilm();
        }
        return null;
    }

    /**
     * Raccourci pour obtenir le titre du film de cet article.
     *
     * @return le titre du film, ou une chaîne vide si le film est absent.
     */
    public String getFilmTitle() {
        Film film = getFilm();
        if (film != null) {
            return film.getTitle();
        }
        return "";
    }

    /**
     * Raccourci pour obtenir l'identifiant du film de cet article.
     *
     * @return l'identifiant du film (String), ou une chaîne vide si absent.
     */
    public String getFilmId() {
        Film film = getFilm();
        if (film != null) {
            return film.getFilm_id();
        }
        return "";
    }

    /** Retourne une représentation textuelle de l'article pour le débogage. */
    @Override
    public String toString() {
        return "CartItem{" +
                "rentalId=" + rentalId +
                ", filmTitle='" + getFilmTitle() + '\'' +
                ", statusId=" + statusId +
                '}';
    }
}
