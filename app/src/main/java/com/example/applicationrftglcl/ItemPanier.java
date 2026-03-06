package com.example.applicationrftglcl;

/**
 * Classe représentant un item dans le panier
 * Contient un film et sa quantité
 */
public class ItemPanier {
    private Film film;
    private int quantite;
    private int rentalId; // ID du rental dans la base de données

    public ItemPanier(Film film, int quantite) {
        this.film = film;
        this.quantite = quantite;
        this.rentalId = -1; // Par défaut, pas de rentalId
    }

    public ItemPanier(Film film, int quantite, int rentalId) {
        this.film = film;
        this.quantite = quantite;
        this.rentalId = rentalId;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getRentalId() {
        return rentalId;
    }

    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }

    // Calculer le prix total pour cet item
    public double getPrixTotal() {
        try {
            double prix = Double.parseDouble(film.getRental_rate());
            return prix * quantite;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
