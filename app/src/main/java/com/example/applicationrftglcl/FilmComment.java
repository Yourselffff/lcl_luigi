package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Modèle de données représentant un commentaire laissé par un client sur un film.
 * Correspond au format JSON retourné par l'API REST (/films/commentaire).
 */
public class FilmComment {

    /** Identifiant unique du commentaire en base de données. */
    @SerializedName("commentId")
    private int commentId;

    /** Identifiant du film auquel ce commentaire est rattaché. */
    @SerializedName("filmId")
    private int filmId;

    /** Identifiant du client ayant rédigé le commentaire. */
    @SerializedName("customerId")
    private int customerId;

    /** Contenu textuel du commentaire. */
    @SerializedName("commentText")
    private String commentText;

    /** Date de création du commentaire (format ISO 8601). */
    @SerializedName("createdDate")
    private String createdDate;

    /** Nom affiché du client (calculé côté serveur). */
    @SerializedName("customerName")
    private String customerName;

    /** Constructeur vide requis par Gson pour la désérialisation JSON. */
    public FilmComment() {
    }

    /**
     * Constructeur utilisé pour créer un nouveau commentaire avant envoi à l'API.
     *
     * @param filmId      identifiant du film concerné.
     * @param customerId  identifiant du client connecté.
     * @param commentText texte du commentaire à soumettre.
     */
    public FilmComment(int filmId, int customerId, String commentText) {
        this.filmId = filmId;
        this.customerId = customerId;
        this.commentText = commentText;
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return l'identifiant du commentaire. */
    public int getCommentId() { return commentId; }

    /** @param commentId identifiant du commentaire à définir. */
    public void setCommentId(int commentId) { this.commentId = commentId; }

    /** @return l'identifiant du film associé. */
    public int getFilmId() { return filmId; }

    /** @param filmId identifiant du film à définir. */
    public void setFilmId(int filmId) { this.filmId = filmId; }

    /** @return l'identifiant du client. */
    public int getCustomerId() { return customerId; }

    /** @param customerId identifiant du client à définir. */
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    /** @return le texte du commentaire. */
    public String getCommentText() { return commentText; }

    /** @param commentText texte du commentaire à définir. */
    public void setCommentText(String commentText) { this.commentText = commentText; }

    /** @return la date de création du commentaire. */
    public String getCreatedDate() { return createdDate; }

    /** @param createdDate date de création à définir. */
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    /** @return le nom du client auteur du commentaire. */
    public String getCustomerName() { return customerName; }

    /** @param customerName nom du client à définir. */
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
