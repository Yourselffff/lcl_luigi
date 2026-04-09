package com.example.applicationrftglcl;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Activity affichant le contenu du panier de l'utilisateur.
 * Charge les articles depuis l'API (GET /cart/{customerId}), les synchronise
 * avec le Singleton Panier, et permet de supprimer des articles, vider
 * ou valider le panier (POST /cart/checkout).
 * Implémente PanierAdapter.PanierChangeListener pour réagir aux modifications
 * déclenchées depuis l'Adapter.
 */
public class PanierActivity extends AppCompatActivity implements PanierAdapter.PanierChangeListener {

    /** ListView affichant les articles du panier. */
    private ListView lvPanier;

    /** TextView affichant le nombre d'articles ("X film(s)"). */
    private TextView tvNombreItems;

    /** TextView affiché quand le panier est vide. */
    private TextView tvPanierVide;

    /** Adapter personnalisé gérant l'affichage de chaque ligne du panier. */
    private PanierAdapter adapter;

    /** Gestionnaire de session pour récupérer le customerId et l'URL serveur. */
    private SessionManager sessionManager;

    /** Liste brute des CartItem retournés par l'API. */
    private ArrayList<CartItem> cartItems;

    /**
     * Initialise l'Activity, crée l'Adapter et charge le panier depuis l'API.
     *
     * @param savedInstanceState état sauvegardé de l'instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        sessionManager = new SessionManager(this);
        cartItems = new ArrayList<>();

        // Liaison des vues avec les éléments du layout
        lvPanier = findViewById(R.id.lvPanier);
        tvNombreItems = findViewById(R.id.tvNombreItems);
        tvPanierVide = findViewById(R.id.tvPanierVide);

        // Création de l'Adapter avec les items du Singleton Panier
        adapter = new PanierAdapter(this, Panier.getInstance().getItems(), this);
        lvPanier.setAdapter(adapter);

        // Chargement initial du panier depuis le serveur
        chargerPanierDepuisAPI();
    }

    /**
     * Rafraîchit l'affichage lors du retour sur cet écran (ex. : retour depuis DetailfilmActivity).
     */
    @Override
    protected void onResume() {
        super.onResume();
        mettreAJourAffichage();
    }

    /**
     * Implémentation de PanierAdapter.PanierChangeListener.
     * Appelé par l'Adapter lorsqu'un article est supprimé.
     */
    @Override
    public void onPanierChanged() {
        mettreAJourAffichage();
    }

    /**
     * Met à jour l'affichage du panier : compteur, visibilité de la liste
     * et du message "panier vide", et notification de l'Adapter.
     */
    private void mettreAJourAffichage() {
        Panier panier = Panier.getInstance();
        int nombreItems = panier.getNombreItems();

        tvNombreItems.setText(nombreItems + " film(s)");

        // Affiche soit la liste soit le message "panier vide"
        if (nombreItems == 0) {
            lvPanier.setVisibility(View.GONE);
            tvPanierVide.setVisibility(View.VISIBLE);
        } else {
            lvPanier.setVisibility(View.VISIBLE);
            tvPanierVide.setVisibility(View.GONE);
        }

        // Notifie l'Adapter pour redessiner les lignes
        adapter.notifyDataSetChanged();
    }

    /**
     * Callback du bouton "Vider le panier" (défini via android:onClick dans le layout).
     * Supprime chaque article via DELETE /cart/{rentalId} si le rentalId est connu,
     * sinon supprime localement.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onViderPanierClicked(View view) {
        ArrayList<ItemPanier> items = Panier.getInstance().getItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "Le panier est déjà vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Copie pour éviter une ConcurrentModificationException pendant l'itération
        ArrayList<ItemPanier> itemsCopy = new ArrayList<>(items);

        for (ItemPanier item : itemsCopy) {
            int rentalId = item.getRentalId();
            if (rentalId > 0) {
                // Suppression via l'API si le rentalId est disponible
                try {
                    URL urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/" + rentalId);
                    new RemoveFromCartTask(this, String.valueOf(rentalId)).execute(urlAAppeler);
                } catch (MalformedURLException mue) {
                    Log.d("mydebug", ">>>RemoveFromCartTask - MalformedURLException: " + mue.toString());
                }
            } else {
                // Suppression locale uniquement si pas de rentalId
                Panier.getInstance().supprimerFilm(item.getFilm().getFilm_id());
            }
        }

        Toast.makeText(this, "Vidage du panier en cours...", Toast.LENGTH_SHORT).show();
        Log.d("PanierActivity", "Vidage du panier - " + itemsCopy.size() + " item(s) à supprimer");
    }

    /**
     * Callback du bouton "Valider la réservation" (défini via android:onClick dans le layout).
     * Appelle POST /cart/checkout pour passer les locations du statut 2 (panier) à 3 (réservé).
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onValiderClicked(View view) {
        if (Panier.getInstance().getNombreItems() == 0) {
            Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }

        int customerId = sessionManager.getCustomerId();
        if (customerId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/checkout");
            new CheckoutCartTask(this, String.valueOf(customerId)).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>CheckoutCartTask - MalformedURLException: " + mue.toString());
            Toast.makeText(this, "Erreur lors de la validation", Toast.LENGTH_SHORT).show();
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par CheckoutCartTask après validation du panier.
     * Vide le panier local et rafraîchit l'affichage en cas de succès.
     *
     * @param resultat réponse de l'API ou "ERROR".
     */
    public void panierValideAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Panier validé: " + resultat);

        if (resultat != null && !resultat.equals("ERROR")) {
            Toast.makeText(this, "Réservation validée !", Toast.LENGTH_LONG).show();
            Log.d("PanierActivity", "Panier validé avec succès");
            // Vide le panier local après confirmation du serveur
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
        } else {
            Toast.makeText(this, "Erreur lors de la validation du panier", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback du bouton "Continuer les achats" (défini via android:onClick dans le layout).
     * Ferme l'Activity et retourne à ListefilmsActivity.
     *
     * @param view vue ayant déclenché l'événement.
     */
    public void onContinuerAchatsClicked(View view) {
        Log.d("PanierActivity", "Retour à la liste des films");
        finish();
    }

    /**
     * Lance GetCartTask pour récupérer le panier depuis l'API.
     * Vérifie que le customerId est valide avant l'appel.
     */
    private void chargerPanierDepuisAPI() {
        int customerId = sessionManager.getCustomerId();

        if (customerId == -1) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/" + customerId);
            new GetCartTask(this, String.valueOf(customerId)).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>GetCartTask - MalformedURLException: " + mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    /**
     * Callback appelé par GetCartTask après récupération du panier.
     * Parse le JSON, synchronise le panier local et rafraîchit l'affichage.
     *
     * @param resultatAppelRest JSON de la liste des CartItem.
     */
    public void mettreAJourPanierApresAppelRest(String resultatAppelRest) {
        Log.d("mydebug", ">>>Panier reçu: " + resultatAppelRest);

        if (resultatAppelRest == null || resultatAppelRest.trim().isEmpty() || resultatAppelRest.equals("[]")) {
            // Panier vide côté serveur : vider aussi localement
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
            return;
        }

        try {
            Gson gson = new Gson();
            Type cartListType = new TypeToken<ArrayList<CartItem>>(){}.getType();
            cartItems = gson.fromJson(resultatAppelRest, cartListType);

            if (cartItems != null && !cartItems.isEmpty()) {
                synchroniserPanierLocal();
            } else {
                Panier.getInstance().viderPanier();
            }
            mettreAJourAffichage();
        } catch (Exception e) {
            Log.e("mydebug", ">>>Erreur parsing panier: " + e.toString());
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
        }
    }

    /**
     * Synchronise le Singleton Panier avec les données reçues de l'API.
     * Recrée les ItemPanier depuis les CartItem et associe chaque rentalId
     * pour permettre la suppression ultérieure via l'API.
     */
    private void synchroniserPanierLocal() {
        Panier.getInstance().viderPanier();

        for (CartItem item : cartItems) {
            Film film = item.getFilm();
            if (film == null) {
                // Crée un Film minimal si l'objet imbriqué est absent de la réponse
                film = new Film();
                film.setFilm_id(item.getFilmId());
                film.setTitle(item.getFilmTitle());
            }

            Panier.getInstance().ajouterFilm(film);

            // Associe le rentalId à l'ItemPanier pour la suppression via DELETE /cart/{rentalId}
            ItemPanier itemPanier = Panier.getInstance().trouverItem(film.getFilm_id());
            if (itemPanier != null) {
                itemPanier.setRentalId(item.getRentalId());
            }
        }
    }

    /**
     * Callback appelé par RemoveFromCartTask après suppression d'un article.
     * Recharge le panier depuis l'API pour synchroniser l'affichage.
     *
     * @param resultat réponse de l'API.
     */
    public void itemRetireDuPanierAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Item retiré du panier: " + resultat);
        Toast.makeText(this, "Film retiré du panier", Toast.LENGTH_SHORT).show();
        // Rechargement pour refléter l'état réel du serveur
        chargerPanierDepuisAPI();
    }
}
