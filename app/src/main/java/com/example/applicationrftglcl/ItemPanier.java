package com.example.applicationrftglcl;

/**
 * Modèle local représentant un article dans le panier de l'utilisateur.
 * Associe un film à une quantité et à son identifiant de location (rentalId)
 * afin de pouvoir interagir avec l'API pour la suppression.
 */
public class ItemPanier {

    /** Film associé à cet article. */
    private Film film;

    /** Quantité de cet article dans le panier. */
    private int quantite;

    /** Identifiant de la location en base (rentalId), -1 si non synchronisé avec l'API. */
    private int rentalId;

    /**
     * Constructeur utilisé à l'ajout local (avant synchronisation API).
     * Le rentalId est initialisé à -1 car il n'est pas encore connu.
     *
     * @param film     film à ajouter.
     * @param quantite quantité initiale.
     */
    public ItemPanier(Film film, int quantite) {
        this.film = film;
        this.quantite = quantite;
        this.rentalId = -1;
    }

    /**
     * Constructeur complet utilisé après synchronisation avec l'API.
     *
     * @param film     film associé.
     * @param quantite quantité.
     * @param rentalId identifiant de la location retourné par l'API.
     */
    public ItemPanier(Film film, int quantite, int rentalId) {
        this.film = film;
        this.quantite = quantite;
        this.rentalId = rentalId;
    }

    // ── Getters et Setters ────────────────────────────────────────────────────

    /** @return le film associé à cet article. */
    public Film getFilm() { return film; }

    /** @param film film à définir. */
    public void setFilm(Film film) { this.film = film; }

    /** @return la quantité de cet article. */
    public int getQuantite() { return quantite; }

    /** @param quantite quantité à définir. */
    public void setQuantite(int quantite) { this.quantite = quantite; }

    /** @return l'identifiant de la location (-1 si non synchronisé). */
    public int getRentalId() { return rentalId; }

    /** @param rentalId identifiant de location à définir. */
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    // ── Méthodes métier ───────────────────────────────────────────────────────

    /**
     * Calcule le prix total pour cet article (tarif unitaire × quantité).
     * Retourne 0 si le tarif n'est pas un nombre valide.
     *
     * @return le prix total en double.
     */
    public double getPrixTotal() {
        try {
            double prix = Double.parseDouble(film.getRental_rate());
            return prix * quantite;
        } catch (NumberFormatException e) {
            // Tarif non parseable (valeur vide ou incorrecte) : on retourne 0
            return 0;
        }
    }
}
