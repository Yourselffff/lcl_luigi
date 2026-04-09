package com.example.applicationrftglcl;

import java.util.ArrayList;

/**
 * Gestionnaire du panier de films — implémente le patron Singleton.
 * Une seule instance est partagée entre toutes les Activities de l'application,
 * garantissant la cohérence des données du panier tout au long de la session.
 */
public class Panier {

    /**
     * Interface de callback permettant de notifier les Activities
     * lorsque le contenu du panier est modifié.
     */
    public interface PanierChangeListener {
        /** Appelé dès qu'un article est ajouté, supprimé ou modifié. */
        void onPanierChanged();
    }

    /** Instance unique (Singleton). */
    private static Panier instance;

    /** Liste des articles présents dans le panier. */
    private ArrayList<ItemPanier> items;

    /** Listener enregistré pour recevoir les notifications de changement. */
    private PanierChangeListener listener;

    /** Constructeur privé — empêche l'instanciation directe (Singleton). */
    private Panier() {
        items = new ArrayList<>();
    }

    /**
     * Retourne l'instance unique du panier, en la créant si nécessaire.
     *
     * @return l'instance Singleton de Panier.
     */
    public static Panier getInstance() {
        if (instance == null) {
            instance = new Panier();
        }
        return instance;
    }

    /**
     * Enregistre le listener à notifier lors des changements du panier.
     *
     * @param listener objet implémentant PanierChangeListener (généralement une Activity).
     */
    public void setListener(PanierChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Déclenche le callback du listener si celui-ci est enregistré.
     * Appelé en interne après chaque modification du panier.
     */
    private void notifierChangement() {
        if (listener != null) {
            listener.onPanierChanged();
        }
    }

    /**
     * Ajoute un film au panier.
     * Si le film est déjà présent, incrémente sa quantité de 1.
     * Sinon, crée un nouvel ItemPanier avec quantité = 1.
     *
     * @param film film à ajouter.
     */
    public void ajouterFilm(Film film) {
        for (ItemPanier item : items) {
            if (item.getFilm().getFilm_id().equals(film.getFilm_id())) {
                // Film déjà dans le panier : incrémenter la quantité
                item.setQuantite(item.getQuantite() + 1);
                notifierChangement();
                return;
            }
        }
        // Nouveau film : ajout avec quantité 1
        items.add(new ItemPanier(film, 1));
        notifierChangement();
    }

    /**
     * Supprime un film du panier par son identifiant.
     *
     * @param filmId identifiant du film à retirer.
     */
    public void supprimerFilm(String filmId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getFilm().getFilm_id().equals(filmId)) {
                items.remove(i);
                notifierChangement();
                return;
            }
        }
    }

    /**
     * Modifie la quantité d'un film dans le panier.
     * Si la nouvelle quantité est ≤ 0, l'article est supprimé.
     *
     * @param filmId           identifiant du film concerné.
     * @param nouvelleQuantite nouvelle quantité à appliquer.
     */
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

    /**
     * Retourne la liste complète des articles du panier.
     *
     * @return ArrayList des ItemPanier.
     */
    public ArrayList<ItemPanier> getItems() {
        return items;
    }

    /** Vide entièrement le panier et notifie le listener. */
    public void viderPanier() {
        items.clear();
        notifierChangement();
    }

    /**
     * Retourne le nombre de références (lignes) différentes dans le panier.
     *
     * @return nombre d'articles distincts.
     */
    public int getNombreItems() {
        return items.size();
    }

    /**
     * Calcule la quantité totale de films (somme de toutes les quantités).
     *
     * @return quantité totale.
     */
    public int getQuantiteTotale() {
        int total = 0;
        for (ItemPanier item : items) {
            total += item.getQuantite();
        }
        return total;
    }

    /**
     * Calcule le montant total du panier (tarif × quantité pour chaque article).
     * Les articles dont le tarif n'est pas parseable sont ignorés.
     *
     * @return prix total en double.
     */
    public double getPrixTotal() {
        double total = 0;
        for (ItemPanier item : items) {
            try {
                double prix = Double.parseDouble(item.getFilm().getRental_rate());
                total += prix * item.getQuantite();
            } catch (NumberFormatException e) {
                // Tarif invalide : ignoré dans le calcul
            }
        }
        return total;
    }

    /**
     * Recherche un article dans le panier par l'identifiant du film.
     *
     * @param filmId identifiant du film recherché.
     * @return l'ItemPanier correspondant, ou null si absent.
     */
    public ItemPanier trouverItem(String filmId) {
        for (ItemPanier item : items) {
            if (item.getFilm().getFilm_id().equals(filmId)) {
                return item;
            }
        }
        return null;
    }
}
