package com.example.applicationrftglcl;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant un commentaire de film
 * Correspond au format JSON retourné par le serveur
 */
public class FilmComment {
    @SerializedName("commentId")
    private int commentId;

    @SerializedName("filmId")
    private int filmId;

    @SerializedName("customerId")
    private int customerId;

    @SerializedName("commentText")
    private String commentText;

    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("customerName")
    private String customerName;

    // Constructeur vide
    public FilmComment() {
    }

    // Constructeur pour créer un nouveau commentaire
    public FilmComment(int filmId, int customerId, String commentText) {
        this.filmId = filmId;
        this.customerId = customerId;
        this.commentText = commentText;
    }

    // Getters et Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
