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
 * Activity pour afficher le panier
 * Principe du cours : utilisation de ListView avec Adapter
 */
public class PanierActivity extends AppCompatActivity implements PanierAdapter.PanierChangeListener {

    private ListView lvPanier;
    private TextView tvNombreItems;
    private TextView tvPanierVide;
    private PanierAdapter adapter;
    private SessionManager sessionManager;
    private ArrayList<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        // Initialiser le SessionManager
        sessionManager = new SessionManager(this);
        cartItems = new ArrayList<>();

        // Initialiser les vues
        lvPanier = findViewById(R.id.lvPanier);
        tvNombreItems = findViewById(R.id.tvNombreItems);
        tvPanierVide = findViewById(R.id.tvPanierVide);

        // Créer l'adapter et l'associer à la ListView
        adapter = new PanierAdapter(this, Panier.getInstance().getItems(), this);
        lvPanier.setAdapter(adapter);

        // Charger le panier depuis l'API
        chargerPanierDepuisAPI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir l'affichage quand on revient sur l'écran
        mettreAJourAffichage();
    }

    // Méthode appelée quand le panier change (principe du cours : callback)
    @Override
    public void onPanierChanged() {
        mettreAJourAffichage();
    }

    // Mettre à jour l'affichage du panier
    private void mettreAJourAffichage() {
        Panier panier = Panier.getInstance();
        int nombreItems = panier.getNombreItems();

        // Afficher le nombre d'items
        tvNombreItems.setText(nombreItems + " film(s)");

        // Afficher ou masquer le message "panier vide"
        if (nombreItems == 0) {
            lvPanier.setVisibility(View.GONE);
            tvPanierVide.setVisibility(View.VISIBLE);
        } else {
            lvPanier.setVisibility(View.VISIBLE);
            tvPanierVide.setVisibility(View.GONE);
        }

        // Notifier l'adapter que les données ont changé
        adapter.notifyDataSetChanged();
    }

    // Bouton "Vider le panier"
    public void onViderPanierClicked(View view) {
        ArrayList<ItemPanier> items = Panier.getInstance().getItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "Le panier est déjà vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // Créer une copie de la liste pour éviter les problèmes de modification pendant l'itération
        ArrayList<ItemPanier> itemsCopy = new ArrayList<>(items);

        // Supprimer chaque item via l'API
        for (ItemPanier item : itemsCopy) {
            int rentalId = item.getRentalId();
            if (rentalId > 0) {
                try {
                    URL urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/" + rentalId);
                    new RemoveFromCartTask(this, String.valueOf(rentalId)).execute(urlAAppeler);
                } catch (MalformedURLException mue) {
                    Log.d("mydebug", ">>>Pour RemoveFromCartTask - MalformedURLException: " + mue.toString());
                }
            } else {
                // Si pas de rentalId, supprimer localement
                Panier.getInstance().supprimerFilm(item.getFilm().getFilm_id());
            }
        }

        Toast.makeText(this, "Vidage du panier en cours...", Toast.LENGTH_SHORT).show();
        Log.d("PanierActivity", "Vidage du panier - " + itemsCopy.size() + " item(s) à supprimer");
    }

    // Bouton "Valider la réservation"
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

        // Appeler l'API pour valider le panier (status 2 → 3)
        URL urlAAppeler = null;
        try {
            urlAAppeler = new URL(sessionManager.getBaseUrl() + "/cart/checkout");
            new CheckoutCartTask(this, String.valueOf(customerId)).execute(urlAAppeler);
        } catch (MalformedURLException mue) {
            Log.d("mydebug", ">>>Pour CheckoutCartTask - MalformedURLException mue=" + mue.toString());
            Toast.makeText(this, "Erreur lors de la validation", Toast.LENGTH_SHORT).show();
        } finally {
            urlAAppeler = null;
        }
    }

    // Callback appelé après la validation du panier
    public void panierValideAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Panier validé: " + resultat);

        if (resultat != null && !resultat.equals("ERROR")) {
            Toast.makeText(this, "Réservation validée !", Toast.LENGTH_LONG).show();
            Log.d("PanierActivity", "Panier validé avec succès");

            // Vider le panier local après validation
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
        } else {
            Toast.makeText(this, "Erreur lors de la validation du panier", Toast.LENGTH_LONG).show();
        }
    }

    // Bouton "Continuer les achats"
    public void onContinuerAchatsClicked(View view) {
        Log.d("PanierActivity", "Retour à la liste des films");
        finish(); // Retour à l'activité précédente
    }

    // Charger le panier depuis l'API
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
            Log.d("mydebug", ">>>Pour GetCartTask - MalformedURLException mue=" + mue.toString());
        } finally {
            urlAAppeler = null;
        }
    }

    // Callback appelé après la récupération du panier depuis l'API
    public void mettreAJourPanierApresAppelRest(String resultatAppelRest) {
        Log.d("mydebug", ">>>Panier reçu: " + resultatAppelRest);

        if (resultatAppelRest == null || resultatAppelRest.trim().isEmpty() || resultatAppelRest.equals("[]")) {
            // Aucun item dans le panier
            Panier.getInstance().viderPanier();
            mettreAJourAffichage();
            return;
        }

        try {
            // Parser le JSON en liste de CartItem
            Gson gson = new Gson();
            Type cartListType = new TypeToken<ArrayList<CartItem>>(){}.getType();
            cartItems = gson.fromJson(resultatAppelRest, cartListType);

            if (cartItems != null && !cartItems.isEmpty()) {
                // Synchroniser avec le panier local pour l'affichage
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

    // Synchroniser le panier local avec les données de l'API
    private void synchroniserPanierLocal() {
        // Vider le panier local
        Panier.getInstance().viderPanier();

        // Ajouter les films du panier API au panier local pour l'affichage
        for (CartItem item : cartItems) {
            // Créer un objet Film à partir du CartItem
            Film film = item.getFilm();
            if (film == null) {
                film = new Film();
                film.setFilm_id(item.getFilmId());
                film.setTitle(item.getFilmTitle());
            }

            // Ajouter au panier local
            Panier.getInstance().ajouterFilm(film);

            // Mettre à jour le rentalId dans l'ItemPanier correspondant
            ItemPanier itemPanier = Panier.getInstance().trouverItem(film.getFilm_id());
            if (itemPanier != null) {
                itemPanier.setRentalId(item.getRentalId());
            }
        }
    }

    // Callback appelé après le retrait d'un item du panier
    public void itemRetireDuPanierAvecSucces(String resultat) {
        Log.d("mydebug", ">>>Item retiré du panier: " + resultat);

        Toast.makeText(this, "Film retiré du panier", Toast.LENGTH_SHORT).show();

        // Recharger le panier depuis l'API
        chargerPanierDepuisAPI();
    }
}
