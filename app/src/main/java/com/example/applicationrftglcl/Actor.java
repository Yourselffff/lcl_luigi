package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle de données représentant un acteur.
 * Les annotations @SerializedName assurent la correspondance entre les champs
 * Java et les propriétés JSON retournées par l'API.
 */
public class Actor {

    /** Identifiant unique de l'acteur en base de données. */
    @SerializedName("actorId")
    private int actorId;

    /** Prénom de l'acteur. */
    @SerializedName("firstName")
    private String firstName;

    /** Nom de famille de l'acteur. */
    @SerializedName("lastName")
    private String lastName;

    /** Date de dernière mise à jour de l'enregistrement. */
    @SerializedName("lastUpdate")
    private String lastUpdate;

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public Actor() {
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'identifiant de l'acteur. */
    public int getActorId() {
        return actorId;
    }

    /** @param actorId identifiant de l'acteur à définir. */
    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    /** @return le prénom de l'acteur. */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom à définir. */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return le nom de famille de l'acteur. */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName nom de famille à définir. */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
     * Retourne le nom complet de l'acteur (prénom + nom).
     * Utilisé notamment dans les listes d'affichage.
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
