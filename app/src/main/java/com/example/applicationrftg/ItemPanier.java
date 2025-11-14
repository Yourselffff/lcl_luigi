package com.example.applicationrftg;

/**
 * Classe représentant un item dans le panier
 * Contient un film et sa quantité
 */
public class ItemPanier {
    private Film film;
    private int quantite;

    public ItemPanier(Film film, int quantite) {
        this.film = film;
        this.quantite = quantite;
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
