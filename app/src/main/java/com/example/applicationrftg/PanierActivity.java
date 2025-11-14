package com.example.applicationrftg;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * Activity pour afficher le panier
 * Principe du cours : utilisation de ListView avec Adapter
 */
public class PanierActivity extends AppCompatActivity implements PanierAdapter.PanierChangeListener {

    private ListView lvPanier;
    private TextView tvNombreItems;
    private TextView tvPrixTotal;
    private TextView tvPanierVide;
    private PanierAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        // Initialiser les vues
        lvPanier = findViewById(R.id.lvPanier);
        tvNombreItems = findViewById(R.id.tvNombreItems);
        tvPrixTotal = findViewById(R.id.tvPrixTotal);
        tvPanierVide = findViewById(R.id.tvPanierVide);

        // Créer l'adapter et l'associer à la ListView
        adapter = new PanierAdapter(this, Panier.getInstance().getItems(), this);
        lvPanier.setAdapter(adapter);

        // Afficher les données du panier
        mettreAJourAffichage();
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

        // Afficher le prix total
        tvPrixTotal.setText(String.format(Locale.FRANCE, "Total: %.2f €", panier.getPrixTotal()));

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
        Panier.getInstance().viderPanier();
        mettreAJourAffichage();
        Toast.makeText(this, "Panier vidé", Toast.LENGTH_SHORT).show();
        Log.d("PanierActivity", "Panier vidé");
    }

    // Bouton "Valider la réservation"
    public void onValiderClicked(View view) {
        if (Panier.getInstance().getNombreItems() == 0) {
            Toast.makeText(this, "Votre panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implémenter l'appel REST pour valider la commande
        Toast.makeText(this, "Réservation validée !", Toast.LENGTH_LONG).show();
        Log.d("PanierActivity", "Panier validé - " + Panier.getInstance().getNombreItems() + " film(s)");

        // Vider le panier après validation
        Panier.getInstance().viderPanier();
        mettreAJourAffichage();
    }

    // Bouton "Continuer les achats"
    public void onContinuerAchatsClicked(View view) {
        Log.d("PanierActivity", "Retour à la liste des films");
        finish(); // Retour à l'activité précédente
    }
}
