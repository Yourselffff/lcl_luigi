package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Modèle de données représentant un film.
 * Correspond au format JSON retourné par l'API REST (/films et /films/{id}).
 * Les annotations @SerializedName font le mapping entre les noms JSON et les champs Java.
 */
public class Film {

    /** Identifiant unique du film. */
    @SerializedName("filmId")
    private String film_id = "";

    /** Titre du film. */
    @SerializedName("title")
    private String title = "";

    /** Synopsis du film. */
    @SerializedName("description")
    private String description = "";

    /** Année de sortie. */
    @SerializedName("releaseYear")
    private String release_year = "";

    /** Identifiant de la langue originale. */
    @SerializedName("originalLanguageId")
    private String language_id = "";

    /** Durée de location en jours. */
    @SerializedName("rentalDuration")
    private String rental_duration = "";

    /** Tarif de location (en devise). */
    @SerializedName("rentalRate")
    private String rental_rate = "";

    /** Durée du film en minutes. */
    @SerializedName("length")
    private String length = "";

    /** Coût de remplacement en cas de perte. */
    @SerializedName("replacementCost")
    private String replacement_cost = "";

    /** Classification du film (ex. : PG, R, G). */
    @SerializedName("rating")
    private String rating = "";

    /** Fonctionnalités spéciales disponibles (ex. : "Trailers, Commentaries"). */
    @SerializedName("specialFeatures")
    private String special_features = "";

    /** Date de dernière mise à jour de l'enregistrement. */
    @SerializedName("lastUpdate")
    private String last_update = "";

    /** Liste des réalisateurs associés au film. */
    @SerializedName("directors")
    private List<Director> directors;

    /** Liste des acteurs du film. */
    @SerializedName("actors")
    private List<Actor> actors;

    /** Liste des catégories du film. */
    @SerializedName("categories")
    private List<Category> categories;

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public Film() {
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'identifiant du film. */
    public String getFilm_id() { return film_id; }

    /** @param film_id identifiant du film à définir. */
    public void setFilm_id(String film_id) { this.film_id = film_id; }

    /** @return le titre du film. */
    public String getTitle() { return title; }

    /** @param title titre à définir. */
    public void setTitle(String title) { this.title = title; }

    /** @return le synopsis du film. */
    public String getDescription() { return description; }

    /** @param description synopsis à définir. */
    public void setDescription(String description) { this.description = description; }

    /** @return l'année de sortie. */
    public String getRelease_year() { return release_year; }

    /** @param release_year année de sortie à définir. */
    public void setRelease_year(String release_year) { this.release_year = release_year; }

    /** @return l'identifiant de la langue originale. */
    public String getLanguage_id() { return language_id; }

    /** @param language_id identifiant de langue à définir. */
    public void setLanguage_id(String language_id) { this.language_id = language_id; }

    /** @return la durée de location en jours. */
    public String getRental_duration() { return rental_duration; }

    /** @param rental_duration durée de location à définir. */
    public void setRental_duration(String rental_duration) { this.rental_duration = rental_duration; }

    /** @return le tarif de location. */
    public String getRental_rate() { return rental_rate; }

    /** @param rental_rate tarif à définir. */
    public void setRental_rate(String rental_rate) { this.rental_rate = rental_rate; }

    /** @return la durée du film en minutes. */
    public String getLength() { return length; }

    /** @param length durée à définir. */
    public void setLength(String length) { this.length = length; }

    /** @return le coût de remplacement. */
    public String getReplacement_cost() { return replacement_cost; }

    /** @param replacement_cost coût de remplacement à définir. */
    public void setReplacement_cost(String replacement_cost) { this.replacement_cost = replacement_cost; }

    /** @return la classification du film. */
    public String getRating() { return rating; }

    /** @param rating classification à définir. */
    public void setRating(String rating) { this.rating = rating; }

    /** @return les fonctionnalités spéciales. */
    public String getSpecial_features() { return special_features; }

    /** @param special_features fonctionnalités spéciales à définir. */
    public void setSpecial_features(String special_features) { this.special_features = special_features; }

    /** @return la date de dernière mise à jour. */
    public String getLast_update() { return last_update; }

    /** @param last_update date de mise à jour à définir. */
    public void setLast_update(String last_update) { this.last_update = last_update; }

    /** @return la liste des réalisateurs. */
    public List<Director> getDirectors() { return directors; }

    /** @param directors liste des réalisateurs à définir. */
    public void setDirectors(List<Director> directors) { this.directors = directors; }

    /** @return la liste des acteurs. */
    public List<Actor> getActors() { return actors; }

    /** @param actors liste des acteurs à définir. */
    public void setActors(List<Actor> actors) { this.actors = actors; }

    /** @return la liste des catégories. */
    public List<Category> getCategories() { return categories; }

    /** @param categories liste des catégories à définir. */
    public void setCategories(List<Category> categories) { this.categories = categories; }

    /**
     * Retourne le titre du film.
     * Utilisé par l'ArrayAdapter pour le filtre de recherche (méthode getFilter).
     */
    @Override
    public String toString() {
        return title;
    }
}
