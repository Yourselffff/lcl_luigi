package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle de données représentant une catégorie de film (ex. : Action, Comédie).
 * Les annotations @SerializedName assurent la correspondance entre les champs
 * Java et les propriétés JSON retournées par l'API.
 */
public class Category {

    /** Identifiant unique de la catégorie en base de données. */
    @SerializedName("categoryId")
    private int categoryId;

    /** Libellé de la catégorie (ex. : "Action", "Drama"). */
    @SerializedName("name")
    private String name;

    /** Date de dernière mise à jour de l'enregistrement. */
    @SerializedName("lastUpdate")
    private String lastUpdate;

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public Category() {
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'identifiant de la catégorie. */
    public int getCategoryId() {
        return categoryId;
    }

    /** @param categoryId identifiant de la catégorie à définir. */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /** @return le nom de la catégorie. */
    public String getName() {
        return name;
    }

    /** @param name nom de la catégorie à définir. */
    public void setName(String name) {
        this.name = name;
    }

    /** @return la date de dernière mise à jour. */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /** @param lastUpdate date de mise à jour à définir. */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Retourne le nom de la catégorie.
     * Utilisé dans les listes déroulantes et les affichages de films.
     */
    @Override
    public String toString() {
        return name;
    }
}
