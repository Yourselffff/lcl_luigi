package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant un réalisateur
 */
public class Director {
    @SerializedName("directorId")
    private int directorId;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    // Constructeur vide
    public Director() {
    }

    // Getters et Setters
    public int getDirectorId() {
        return directorId;
    }

    public void setDirectorId(int directorId) {
        this.directorId = directorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
