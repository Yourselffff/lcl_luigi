package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle de données représentant un exemplaire physique d'un film en stock.
 * Un film peut posséder plusieurs entrées Inventory (un par exemplaire disponible).
 */
public class Inventory {

    /** Identifiant unique de l'exemplaire en base de données. */
    @SerializedName("inventoryId")
    private int inventoryId;

    /** Identifiant du film associé à cet exemplaire. */
    @SerializedName("filmId")
    private int filmId;

    /** Identifiant du magasin qui détient cet exemplaire. */
    @SerializedName("storeId")
    private int storeId;

    /** Date de dernière mise à jour de l'enregistrement. */
    @SerializedName("lastUpdate")
    private String lastUpdate;

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public Inventory() {
    }

    /**
     * Constructeur complet utilisé pour créer un exemplaire manuellement.
     *
     * @param inventoryId identifiant de l'exemplaire.
     * @param filmId      identifiant du film associé.
     * @param storeId     identifiant du magasin.
     * @param lastUpdate  date de dernière mise à jour.
     */
    public Inventory(int inventoryId, int filmId, int storeId, String lastUpdate) {
        this.inventoryId = inventoryId;
        this.filmId = filmId;
        this.storeId = storeId;
        this.lastUpdate = lastUpdate;
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'identifiant de l'exemplaire. */
    public int getInventoryId() {
        return inventoryId;
    }

    /** @param inventoryId identifiant de l'exemplaire à définir. */
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    /** @return l'identifiant du film associé. */
    public int getFilmId() {
        return filmId;
    }

    /** @param filmId identifiant du film à définir. */
    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    /** @return l'identifiant du magasin. */
    public int getStoreId() {
        return storeId;
    }

    /** @param storeId identifiant du magasin à définir. */
    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    /** @return la date de dernière mise à jour. */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /** @param lastUpdate date de mise à jour à définir. */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /** Retourne une représentation textuelle de l'exemplaire pour le débogage. */
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
