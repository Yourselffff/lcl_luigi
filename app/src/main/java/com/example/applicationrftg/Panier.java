package com.example.applicationrftg;

import java.util.ArrayList;

/**
 * Classe Singleton pour gérer le panier de films
 * Utilise un ArrayList pour stocker les films sélectionnés
 */
public class Panier {

    // Interface pour notifier les changements du panier
    public interface PanierChangeListener {
        void onPanierChanged();
    }

    // Instance unique (Singleton)
    private static Panier instance;

    // ArrayList pour stocker les films avec leur quantité
    private ArrayList<ItemPanier> items;

    // Listener pour notifier les changements
    private PanierChangeListener listener;

    // Constructeur privé pour le Singleton
    private Panier() {
        items = new ArrayList<>();
    }

    // Méthode pour obtenir l'instance unique
    public static Panier getInstance() {
        if (instance == null) {
            instance = new Panier();
        }
        return instance;
    }

    // Définir le listener pour les changements
    public void setListener(PanierChangeListener listener) {
        this.listener = listener;
    }

    // Notifier les changements
    private void notifierChangement() {
        if (listener != null) {
            listener.onPanierChanged();
        }
    }

    // Ajouter un film au panier
    public void ajouterFilm(Film film) {
        // Vérifier si le film existe déjà dans le panier
        for (ItemPanier item : items) {
            if (item.getFilm().getFilm_id().equals(film.getFilm_id())) {
                // Augmenter la quantité
                item.setQuantite(item.getQuantite() + 1);
                notifierChangement();
                return;
            }
        }
        // Si le film n'existe pas, l'ajouter avec quantité 1
        items.add(new ItemPanier(film, 1));
        notifierChangement();
    }

    // Supprimer un film du panier
    public void supprimerFilm(String filmId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getFilm().getFilm_id().equals(filmId)) {
                items.remove(i);
                notifierChangement();
                return;
            }
        }
    }

    // Modifier la quantité d'un film
    public void modifierQuantite(String filmId, int nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            supprimerFilm(filmId);
            return;
        }

        for (ItemPanier item : items) {
            if (item.getFilm().getFilm_id().equals(filmId)) {
                item.setQuantite(nouvelleQuantite);
                notifierChangement();
                return;
            }
        }
    }

    // Obtenir tous les items du panier
    public ArrayList<ItemPanier> getItems() {
        return items;
    }

    // Vider le panier
    public void viderPanier() {
        items.clear();
        notifierChangement();
    }

    // Obtenir le nombre total d'items
    public int getNombreItems() {
        return items.size();
    }

    // Obtenir la quantité totale de films
    public int getQuantiteTotale() {
        int total = 0;
        for (ItemPanier item : items) {
            total += item.getQuantite();
        }
        return total;
    }

    // Calculer le prix total
    public double getPrixTotal() {
        double total = 0;
        for (ItemPanier item : items) {
            // Convertir rental_rate en double et multiplier par quantité
            try {
                double prix = Double.parseDouble(item.getFilm().getRental_rate());
                total += prix * item.getQuantite();
            } catch (NumberFormatException e) {
                // Si conversion échoue, ignorer
            }
        }
        return total;
    }
}
