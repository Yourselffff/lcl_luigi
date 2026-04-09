package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle de données représentant un réalisateur.
 * Les annotations @SerializedName assurent la correspondance entre les champs
 * Java et les propriétés JSON retournées par l'API.
 */
public class Director {

    /** Identifiant unique du réalisateur en base de données. */
    @SerializedName("directorId")
    private int directorId;

    /** Prénom du réalisateur. */
    @SerializedName("firstName")
    private String firstName;

    /** Nom de famille du réalisateur. */
    @SerializedName("lastName")
    private String lastName;

    /** Date de dernière mise à jour de l'enregistrement. */
    @SerializedName("lastUpdate")
    private String lastUpdate;

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public Director() {
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'identifiant du réalisateur. */
    public int getDirectorId() {
        return directorId;
    }

    /** @param directorId identifiant du réalisateur à définir. */
    public void setDirectorId(int directorId) {
        this.directorId = directorId;
    }

    /** @return le prénom du réalisateur. */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName prénom à définir. */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return le nom de famille du réalisateur. */
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
     * Retourne le nom complet du réalisateur (prénom + nom).
     * Utilisé notamment dans les listes d'affichage.
     */
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
