package com.example.applicationrftg;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Classe représentant un film
 * Correspond au format JSON retourné par le serveur
 */
public class Film {
    @SerializedName("filmId")
    private String film_id = "";

    @SerializedName("title")
    private String title = "";

    @SerializedName("description")
    private String description = "";

    @SerializedName("releaseYear")
    private String release_year = "";

    @SerializedName("originalLanguageId")
    private String language_id = "";

    @SerializedName("rentalDuration")
    private String rental_duration = "";

    @SerializedName("rentalRate")
    private String rental_rate = "";

    @SerializedName("length")
    private String length = "";

    @SerializedName("replacementCost")
    private String replacement_cost = "";

    @SerializedName("rating")
    private String rating = "";

    @SerializedName("specialFeatures")
    private String special_features = "";

    @SerializedName("lastUpdate")
    private String last_update = "";

    @SerializedName("directors")
    private List<Director> directors;

    @SerializedName("actors")
    private List<Actor> actors;

    @SerializedName("categories")
    private List<Category> categories;

    // Constructeur vide
    public Film() {
    }

    // Getters et Setters
    public String getFilm_id() {
        return film_id;
    }

    public void setFilm_id(String film_id) {
        this.film_id = film_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelease_year() {
        return release_year;
    }

    public void setRelease_year(String release_year) {
        this.release_year = release_year;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getRental_duration() {
        return rental_duration;
    }

    public void setRental_duration(String rental_duration) {
        this.rental_duration = rental_duration;
    }

    public String getRental_rate() {
        return rental_rate;
    }

    public void setRental_rate(String rental_rate) {
        this.rental_rate = rental_rate;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getReplacement_cost() {
        return replacement_cost;
    }

    public void setReplacement_cost(String replacement_cost) {
        this.replacement_cost = replacement_cost;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSpecial_features() {
        return special_features;
    }

    public void setSpecial_features(String special_features) {
        this.special_features = special_features;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    // Méthode toString pour le filtre de recherche
    @Override
    public String toString() {
        return title;
    }
}
