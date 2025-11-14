package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant un acteur
 */
public class Actor {
    @SerializedName("actorId")
    private int actorId;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    // Constructeur vide
    public Actor() {
    }

    // Getters et Setters
    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
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
